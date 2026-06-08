测试用的项目

Spring Boot 4 相关

## Docker 部署与运行

本项目的 `application.yml` 使用了环境变量占位（见 [src/main/resources/application.yml](src/main/resources/application.yml)），运行容器时需向容器传入这些变量，或使用 `--env-file`。

主要环境变量（在 `application.yml` 中引用）：

- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PWD`
- `PG_URL`（例如 `jdbc:postgresql://db:5432/dbname`）
- `PG_USER`
- `PG_PWD`

构建镜像：
```bash
docker build -t zisnackdesk:latest .
```

运行容器（Bash/Unix 示例，推荐挂载 dumps 目录以持久化 heap dump）：
```bash
mkdir -p ./dumps
docker run -d --name zisnackdesk \
	-p 5000:5000 \
	--env-file .env \
	-v $(pwd)/dumps:/app/dumps \
	--log-opt max-size=10m --log-opt max-file=5 \
	zisnackdesk:latest
```

Windows PowerShell 示例：
```powershell
New-Item -ItemType Directory -Force -Path .\dumps
docker run -d --name zisnackdesk `
	-p 5000:5000 `
	--env-file .env `
	-v ${PWD}/dumps:/app/dumps `
	--log-opt max-size=10m --log-opt max-file=5 `
	zisnackdesk:latest
```

说明要点：

- GC 日志已在镜像中配置为输出到 stdout（镜像内默认 JVM 参数使用 `-Xlog:gc*:file=/proc/self/fd/1`），因此可用 `docker logs -f zisnackdesk` 或由日志收集系统抓取。
- Heap dump（`.hprof`）为二进制文件，写入目录 `/app/dumps`；必须挂载该目录以便持久化和下载（若未挂载，可用 `docker cp` 从容器取回）。
- 若宿主机目录权限导致写入失败，请确保宿主目录对容器运行用户可写（可先 `chown` 或短期设置为 0777）。
- 如需自定义 JVM 参数，可在运行时通过环境变量 `JAVA_OPTS` 覆盖镜像内默认值。

Docker Compose 示例：
```yaml
version: '3.8'
services:
	app:
		image: zisnackdesk:latest
		ports:
			- '5000:5000'
		env_file:
			- .env
		volumes:
			- ./dumps:/app/dumps
		logging:
			driver: 'json-file'
			options:
				max-size: '10m'
				max-file: '5'
```

快速操作汇总：
```bash
# 构建镜像
docker build -t zisnackdesk:latest .

# 运行（使用示例 .env）
docker run -d --name zisnackdesk -p 5000:5000 --env-file .env -v $(pwd)/dumps:/app/dumps zisnackdesk:latest

# 查看日志（包含 GC 日志）
docker logs -f zisnackdesk

# 若未挂载 dumps，获取 dump 文件：
docker cp zisnackdesk:/app/dumps/heap.hprof ./heap.hprof
```

更多细节请参见： [src/main/resources/application.yml](src/main/resources/application.yml)
