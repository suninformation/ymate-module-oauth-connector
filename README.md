### YMP-OAuth-Connector

基于YMP框架实现的第三方OAuth授权登录模块, 目前已实现:

|授权名称|状态|
|---|---|
|github|通过|
|weibo|通过|
|baidu|通过|
|qq|通过|
|wechat|通过|
|gitosc|通过|
|oschina|通过|
|google|待实现|
|linkedin|待实现|
|windowslive|待实现|

#### Maven包依赖

    <dependency>
        <groupId>net.ymate.module</groupId>
        <artifactId>ymate-module-oauth-connector</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>


#### 搭建模块工程

- 首先，你需要创建一个基于YMPv2框架的JavaWeb工程项目；（[如何快速搭建工程?](http://git.oschina.net/suninformation/ymate-platform-v2/wikis/Quickstart_New)）

- YMP框架扫描包路径要包含`net.ymate.module.oauth.connector`, 调整配置如下:

        # 框架自动扫描的包路径集合，多个包名之间用'|'分隔，默认已包含net.ymate.platform包，其子包也将被扫描
        ymp.autoscan_packages=net.ymate

- 调整`WebMvc`模块中配置如下：

        # 控制器请求映射路径分析器，，可选值为已知分析器名称或自定义分析器类名称，默认为default，目前支持已知分析器[default|restful|...]
        ymp.configs.webmvc.request_mapping_parser_class=restful

### 模块配置参数说明

    #-------------------------------------
    # module.oauth.connector 模块初始化参数
    #-------------------------------------
    
    # 缓存名称前缀, 默认值: ""
    ymp.configs.module.oauth.connector.cache_name_prefix=
    
    # 连接授权回调处理器, 默认值为net.ymate.cms.oauth.connector.impl.DefaultConnectCallbackHandler
    ymp.configs.module.oauth.connector.callback_handler_class=
    
    # 密码类参数是否已加密, 默认值: false
    ymp.configs.module.oauth.connector.password_encrypted=
    
    # 密码处理器, 可选参数, 用于对已加密OAuth客户端密钥进行解密，默认值: 空
    ymp.configs.module.oauth.connector.password_class=
    
    #--------------------------
    # 以下为各连接器的初始化配置, xxx表示连接器名称, 目前支持[github|wechat|qq|weibo|oschina|baidu|google|linkedin|windowslive]
    #--------------------------
    
    # OAuth客户端ID, 必选项
    ymp.configs.module.oauth.connector.xxx.client_id=
    
    # OAuth客户端密钥, 必选项
    ymp.configs.module.oauth.connector.xxx.client_secret=
    
    # 重定向URL地址, 可选参数, 默认值: 空
    ymp.configs.module.oauth.connector.xxx.redirect_uri=

#### 启动并测试

- 申请并配置好OAuth客户端ID和密钥(访问`https://github.com/settings/developers`进行申请);

- 在申请过程中需要填写`redirect_uri`地址, 请填写: `http://<你的域名>/oauth/connect/github/redirect`;

- 打包工程并部署到目标服务器;

- 打开浏览器并输入网址, 如: `http://<你的域名>/oauth/connect/github` 进行授权测试;

- 如果OAuth请求授权成功，则默认`callback_handler_class`处理器将为你显示回应的JSON数据, 接下来请自行编写`IOAuthConnectCallbackHandler`接口实现来存储授权信息吧:)

#### One More Thing

YMP不仅提供便捷的Web及其它Java项目的快速开发体验，也将不断提供更多丰富的项目实践经验。

感兴趣的小伙伴儿们可以加入 官方QQ群480374360，一起交流学习，帮助YMP成长！

了解更多有关YMP框架的内容，请访问官网：http://www.ymate.net/