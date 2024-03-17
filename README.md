# Sangchu Passport

[![](https://jitpack.io/v/CoffeeDrivenDevelopment/sangchu-passport.svg)](https://jitpack.io/#CoffeeDrivenDevelopment/sangchu-passport)

해당 서비스는 `MSA`로 이루어져 있습니다. 각 서버마다 사용자인증을 하게 된다면 중복되는 코드가 많이지게 됩니다.
또한 각 클라이언트마다 인증 객체가 다를 수 있으므로 상추에서는 각각 다른 클라이언트의 인증 요청을 하나의 `Passport`로 묶어 서버간의 인증을 단순화 하였습니다.

## Quick Starter

### 1. Add Dependencies

---

반드시 `Redis`가 필요합니다.

**build.gradle**

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }  // Add Repository
}

dependencies {
    // More dependencies
    implementation 'org.springframework.boot:spring-boot-starter-data-redis-reactive'
    implementation 'com.github.CoffeeDrivenDevelopment:sangchu-passport:0.0.7'
}
```

**build.gradle.kts**

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io") // Add Repository
}

dependencies {
    // More dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("com.github.CoffeeDrivenDevelopment:sangchu-passport:0.0.7")
}
```

### 2. PassportTokenRepository 생성

---

`Redis`에서 `Passport`를 재사용하지 못하도록 `PassportToken`을 저장하는 `Repository`를 생성합니다.

**PassportTokenRepository.java**

```java
public interface PassportTokenRepository extends CrudRepository<PassportToken, String> {
}
```

**PassportTokenRepository.kt**

```kotlin
interface PassportTokenRepository : CurdRepository<PassportToken, String>
```

### 3. PassportConfig

---

`Passport`를 검증하는 컴포넌트를 `Bean`으로 등록합니다.

**PassportConfig.java**

```java

@RequiredArgsConstructor
@Configuration
public class PassportConfig {
    private final PassportTokenRepository passportTokenRepository;

    @Value("${spring.application.name}")
    private String name;

    @Bean
    public PassportValidator passportValidator() {
        return new PassportValidator(passportTokenRepository, name);
    }

    @Bean
    public PassportAdvice passportAdvice() {
        return new PassportAdvice();
    }
}
```

**PassportConfig.kt**

```kotlin
@Configuration
class PassportConfig(
    private val passportTokenRepository: PassportTokenRepository,
    @Value("${spring.application.name}") private val name: String
) {
    @Bean
    fun passportValidator(): PassportValidator {
        return PassportValidator(passportTokenRepository, name)
    }

    @Bean
    fun passportAdvice(): PassportAdvice {
        return PassportAdvice()
    }
}
```

### 4. PassportArgumentResolversConfig

---

**PassportArgumentResolversConfig.java**

```java

@RequiredArgsConstructor
@Configuration
public class PassportArgumentResolversConfig implements WebMvcConfigurer {
    private final ObjectMapper mapper;
    private final PassportValidator validator;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PassportArgumentResolver(mapper, validator));
    }
}
```

**PassportArgumentResolversConfig.kt**

```kotlin
@Configuration
class PassportArgumentResolversConfig(
    private val mapper: ObjectMapper,
    private val validator: PassportValidator
) : WebMvcConfigurer {

    @Override
    fun addArgumentResolvers(resolvers: List<HandlerMethodArgumentResolver>) {
        resolvers.add(PassportArgumentResolver(mapper, validator))
    }
}
```

### 5. Controller

---

기본적인 설정이 끝났습니다. 이제 해당 기능을 사용해보도록 하겠습니다.
`@RequestPassport`를 아래 코드와 같이 설정하면 됩니다.

**java**

```java

@GetMapping("/v1")
public ResponseEntity<Void> passport(@RequestPassport Passport passport) {
    return ResponseEntity.ok()
            .headers(passport.getHeaders())
            .body(passport);
}
```

**kotlin**

```kotlin
@GetMapping("/v1")
fun passport(@RequestPassport passport: Passport): ResponseEntity<Void> {
    return ResponseEntity.ok()
        .headers(passport.getHeaders())
        .body(passport)
}
```