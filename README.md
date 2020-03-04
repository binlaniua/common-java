
### usage
```xml
<repository>
    <id>common-java</id>
    <url>https://raw.github.com/binlaniua/common-java/mvn-repo</url>
</repository>
```

```xml
<dependency>
    <groupId>cn.tkk</groupId>
    <artifactId>common</artifactId>
    <version>1.1</version>
</dependency>
```


### scan

```java
@SpringBootApplication(scanBasePackages = {
    "cn.tkk.common",
    "xxxx"
})
```

### 