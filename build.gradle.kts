import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Property

val postgresDriverVersion = "42.2.24"
val jsonwebtokenVersion = "0.11.2"

plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"

    id("org.flywaydb.flyway") version "8.0.1"
    id("nu.studer.jooq") version "6.0.1"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

jooq {
    version.set("3.14.7")

    configurations {
        create("xml") {
            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "${project.properties.getOrDefault("dbUrl", "jdbc:postgresql://ec2-54-216-48-43.eu-west-1.compute.amazonaws.com:5432/d7fa1v3opctc8o")}"
                    user = "${project.properties.getOrDefault("dbUser", "tcoquqmanznuot")}"
                    password = "${project.properties.getOrDefault("dbPassword", "bcb307b99214272e30aae6b2955e7e559830e32f9f924a2e88a648027f2c0461")}"
                }
                generator.apply {
                    name = "org.jooq.codegen.XMLGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        includes = ".*"
                        schemata.add(org.jooq.meta.jaxb.SchemaMappingType().apply {
                            inputSchema = "calendar"
                        })
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        directory = "src/main/resources/jooq/calendar"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }

        create("code") {
            generateSchemaSourceOnCompilation.set(false)

            jooqConfiguration.apply {
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.xml.XMLDatabase"
                        includes = ".*"
                        inputSchema = "calendar"
                        properties.apply {
                            add(Property().withKey("dialect").withValue("POSTGRES"))
                            add(
                                Property().withKey("xmlFile")
                                    .withValue("src/main/resources/jooq/calendar/org/jooq/generated/information_schema.xml")
                            )
                        }
                    }
                    generate.apply {
                        isRelations = true
                        isDeprecated = false
                        isRecords = true
                        isPojos = true
                        isFluentSetters = true
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        packageName = "com.example.joomtest.jooq.calendar"
                        directory = "src/main/java"
                    }
                }
            }
        }
    }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:$jsonwebtokenVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jsonwebtokenVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jsonwebtokenVersion")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    // DB
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    runtimeOnly("org.postgresql:postgresql:$postgresDriverVersion")
    jooqGenerator("org.postgresql:postgresql:$postgresDriverVersion")
    implementation("org.flywaydb:flyway-core")

    // Caching
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:3.0.3")

    // Swagger
    implementation("io.springfox:springfox-boot-starter:3.0.0")

    // Test
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

flyway {
    url = "jdbc:postgresql://ec2-54-216-48-43.eu-west-1.compute.amazonaws.com:5432/d7fa1v3opctc8o"
    user = "tcoquqmanznuot"
    password = "bcb307b99214272e30aae6b2955e7e559830e32f9f924a2e88a648027f2c0461"
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks {
    "compileKotlin" {
        dependsOn("generateCodeJooq")
    }
}
