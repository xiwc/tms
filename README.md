# lhjz.portal
立衡脊柱门户网站(http://czs.github.io/lhjz.portal)

---

### 2015/3/28

Initial commit
>
1. maven build - version:3.2.1
2. spring tool suite(sts) - version:3.6.4.RELEASE
3. jdk-1.8
4. git
5. mysql-5.6
6. junit test

Dependency
>
1. spring boot starter
2. spring-boot-starter-actuator
3. spring-boot-starter-aop
4. spring-boot-starter-data-jpa
5. spring-boot-starter-web
6. mysql-connector-java
7. spring-boot-starter-tomcat
8. spring-boot-starter-test

### 2015/3/29

框架结构构建
>
1. reset web controller
2. service
3. jpa repository
4. connect to real `mysql` instance
5. add `testng` test(replace default `junit` test)
6. add jpa transaction
7. add apache commons `dbcp` connection pool
8. add `json-path` lib dependency
9. add `apache commons-io|beanutils|lang|connections` lib dependency

### 2015/3/30

框架结构构建
>
1. add `dao|base|component|exception|pojo` package
2. split `domain` package to `entity` and `repository` package
3. add apache commons `dbutils` lib dependency

### 2015/3/31

框架结构构建
>
1. add `@PersistenceContext EntityManager`
2. change base test to `AbstractTransactionTestNGSpringContextTests`
3. rename base test class
4. test spring data `jpa transaction`
5. test `jdbcTemplate`
6. test `collection stream lambda`
7. add `i18n` support
8. add `logback.xml` configuration file
9. change i18n properties file to charset `ISO-8859-1`
10. append dir `log/` to .gitignore file
11. add history to `readme.md`

### 2015/4/1

框架结构构建
>
1. add lib dependency `spring-boot-starter-thymeleaf`
2. add view template `Thymeleaf 2.1.4`
3. add [thymeleaf eclipse plugin](https://github.com/thymeleaf/thymeleaf-extras-eclipse-plugin#adding-content-assist-for-your-dialect)
4. add test template view page `home.html`

### 2015/4/2

框架结构构建
>
1. seprate i18n properties file
2. add `doc\xhtml1-strict-thymeleaf-spring4-4.dtd` for `content assist` when thymeleaf page design
3. add base class for `controller&dao&service`
4. change in application.properties `spring.messages.encoding=ISO-8859-1`
5. add thymeleaf test template view `test.html`
6. add dev-framework related docs to dir `doc\`
7. use `external tomcat8` run webapp, add `<Context path="/" reloadable="true" docBase=".../webapp" />` to `Host` node of `conf/server.xml`
8. run maven command `mvn clean package -Dmaven.test.skip=true` to package project
9. thymeleaf expressions test `Link URL Expressions: @{...}`

### 2015/4/3

框架结构构建
>
1. upgrade `spring-boot-starter-parent` to `1.2.31.2.3.RELEASE`
2. move `static & templates` dir to webapp
3. add `lhjz` static html page to `templates`
4. change prefix of thymeleaf templates as `spring.thymeleaf.prefix=/WEB-INF/templates/`
5. add javascript lib to webapp `static` dir

### 2015/4/4

框架结构构建
>
1. move `static resources` in webapp content to `classpath`
2. combine `thymeleaf` with landing `index.html`
3. add web project `favicon.ico`
4. remove `spring-boot-starter-actuator` lib dependency
5. combine `thymeleaf` with others remaining landing pages

### 2015/4/5

框架结构构建
>
1. add profile `dev` & `prod` for development and deploy product
2. add `spring.profiles.active=dev` into `application.properties` file
3. add `data.sql` for initializing database when launching webapp
4. enable `spring-boot-starter-actuator` lib dependency
5. add `janino` lib dependency for logback's <if>-<then>-<else> expression
6. `LandingController` add `landing` context prefix
7. set `logging.path` & `logging.file` properties for `logback.xml`'s context variable
8. change logback.xml configuration with <if>-<then>-<else> expression
9. add prefix action href `landing` into `common.html` file

### 2015/4/6

框架结构构建
>
1. set `management.context-path`
2. merge `LandingController & RootController`
3. romve prefix context url in `common.html`
4. resolve run tomcat failed problem - `common user` cannot use `80` port
5. add `spring security` control for `/admin` manager
6. remove some unused files

### 2015/4/8

框架结构构建
>
1. add mysql database for user authentication
2. add a Password Encoder(`BCryptPasswordEncoder`)
3. tidy up the static resources

### 2015/4/19

框架结构构建
>
1. add admin manager back-end UI framework(**semantic-ui**)

### 2015/4/20

框架结构构建
>
1. back-end admin `index.html` page enhancement

### 2015/4/23

框架结构构建
>
1. change `spring.thymeleaf.mode=LEGACYHTML5`
2. add admin pages
3. active select menu
4. logout form modification

### 2015/4/25

框架结构构建
>
1. add static lib resources
2. reconfigure security config
3. impl file upload feature
4. add some maven dependencies
5. add some java helper util classes
6. 图片删除 
7. 幻灯图片展示 
8. 图片上传
9. upload multiple support
10.页面在移动浏览器下禁止缩放

### 2015/4/26

功能设计开发
>
1. 文件名修改
2. js自定义工具方法 `indow.Utils.*()`
3. @Entity 属性注解验证
4. Action Form属性注解验证
5. java `FileUtil.java`添加工具方法

### 2015/4/27

框架结构构建
>
1. `maven-tomcat7-plugin` for deploying to remote server
2. clean `webapp/upload/` and include `resources/` to package war.
3. `springloaded` plugin for hot java class deploy

### 2015/5/6

框架结构构建
>
1. 使用java entity定义用户验证授权的数据库表结构

### 2015/6/5

框架结构构建
>
1. JsonPath: http://goessner.net/articles/JsonPath/
2. thymeleaf-extras-springsecurity: https://github.com/thymeleaf/thymeleaf-extras-springsecurity

# TODO
>
1. more thymeleaf `tags & expressions` test.
3. try to launch the webapp using `jar` package mode(by `inner tomcat` and run through `main` method).
6. admin manager gui part
