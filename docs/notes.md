# Notes

This is a java project. How do you create one?

From repo root run

```bash
 mvn archetype:generate --define groupId=com.sintutu.organicshopuitests --define artifactId=organicshopuitests --define archetypeVersion=1.5 --define interactiveMode=false
 ```

How do you add the Selenium dependency?

Edit ./organicshopuitests/pom.xml as follows:

1. Add this dependency to the `dependencies` tag
```xml
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>${selenium.version}</version>
    </dependency>
```

2. Add this property to the `properties` tag

```xml
    <selenium.version>4.29.0</selenium.version>
```

3. Run the command

```bash
mvn --file ./organicshopuitests/pom.xml test-compile
```

and the Selenium dependencies appear.