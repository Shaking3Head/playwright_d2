# Command
```js
// 参数login 指明是否走重新登录的方式 重新登录会刷新本地cookie信息
mvn exec:java -e -D exec.mainClass=org.xmlq.App -D exec.args="login"
// 使用已登录的cookie 不指定login参数即可
mvn exec:java -e -D exec.mainClass=org.xmlq.App
```
