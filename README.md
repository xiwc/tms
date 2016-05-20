# TMS
翻译管理系统(http://192.168.7.253:7990/projects/STEP/repos/tms/browse)

# DEMO访问
http://translation.sh1.newtouch.com/admin/login

# 使用技术
视图UI: http://semantic-ui.com/  
模板引擎: http://www.thymeleaf.org/  
后台MVC: http://projects.spring.io/spring-boot/  
数据持久化: http://projects.spring.io/spring-data-jpa/  
安全认证: http://projects.spring.io/spring-security/  
数据库: mysql  
JDK: jdk8  

# 开发工具
sts: http://spring.io/tools/sts/  
sublime text3: http://www.sublimetext.com/3  

# 开发配置
修改配置文件(数据库相关): src/main/resources/application-dev.properties  
spring.datasource.url=  
spring.datasource.username=  
spring.datasource.password=  

修改配置文件(邮件相关): src/main/resources/application-tms.properties  
lhjz.mail.switch=on  
lhjz.mail.server.host=smtp.163.com  
lhjz.mail.server.port=25  
lhjz.mail.sender.username=  
lhjz.mail.sender.password=  
lhjz.mail.sender.from.address=  
lhjz.mail.to.addresses=  

# 内置用户
admin/888888 角色:超级管理员  
tms/888888 角色:管理员  
test/888888 角色:普通用户  

 
