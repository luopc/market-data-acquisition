data-acquisition-service/
├── src/main/java/com/bank/fx/dataacquisition/
│   ├── config/                 # 配置类
│   ├── entity/                 # 数据实体
│   ├── dto/                    # 数据传输对象
│   ├── adapter/                # 数据源适配器
│   │   ├── impl/               # 具体适配器实现
│   │   └── factory/            # 适配器工厂
│   ├── service/                # 业务服务
│   ├── repository/             # 数据访问层
│   ├── scheduler/              # 定时任务
│   ├── integration/            # Spring Integration配置
│   ├── validation/             # 数据验证
│   └── exception/              # 异常处理
├── src/main/resources/
│   ├── application.yml
│   └── bank-config/
└── pom.xml
