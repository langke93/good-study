# DubboREST
2019/12/12  
* 基于标准的Java REST API——JAX-RS 2.0（Java API for RESTful Web Services的简写）实现的REST调用支持 [http://dubbo.apache.org/zh-cn/docs/user/references/protocol/rest.html](http://dubbo.apache.org/zh-cn/docs/user/references/protocol/rest.html)  

基于rest dubbo服务调用示例：  
```
curl http://localhost:8081/users/7  
curl http://localhost:8081/users/register -XPOST  -H "Content-Type:application/json" -d'{"userName":"user1"}'  
```