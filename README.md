# secret-spring-boot-starter
配置文件敏感信息加解密

# bootstrap.yaml/application.yaml /nacos文件 可以跟进配置的 Encrypted:前缀解密数据
test:
  secret:
    mysql:
      # 加密数据
      user: Encrypted: E2Z3gvXufRIwuif2RfgkNQ==
      demo: Encrypted: Encrypted: E2Z3gvXufRIwuif2RfgkNQ==
      password: Encrypted: E2Z3gvXufRIwuif2RfgkNQ==
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://10.10.7.11:3306/ksher_config_dev?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=true
    username: Encrypted: E2Z3gvXufRIwuif2RfgkNQ==
    password: Encrypted: E2Z3gvXufRIwuif2RfgkNQ==
# 在PropertySourceLoader的时候解密
![image](https://github.com/user-attachments/assets/9c99c373-7ebc-41ef-a590-589e97066694)
