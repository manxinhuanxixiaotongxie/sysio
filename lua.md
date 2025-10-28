flowchart TD
A[开始] --> B{缓存 is_gray 是否为 false}
B -->|是| C[执行默认环境]
B -->|否| D{缓存 is_gray_user 是否存在}
D -->|是| E{is_gray_user 是否为 true}
E -->|是| F[设置灰度 Cookie 并执行灰度环境]
E -->|否| G[设置正常 Cookie 并执行正常环境]
D -->|否| H{缓存 gray_route 是否存在}
H -->|存在| I[执行灰度路由环境]
H -->|不存在| J{缓存 gray_ip_regexp 是否存在}
J -->|存在| K{请求 IP 是否匹配正则}
K -->|匹配| L[设置灰度 Cookie 并执行灰度环境]
K -->|不匹配| M[设置正常 Cookie 并执行正常环境]
J -->|不存在| N{Redis 数据是否存在}
N -->|存在| O[遍历 Redis 数据]
O -->|匹配到 all| P[设置灰度 Cookie 并执行灰度环境]
O -->|匹配到 none| Q[设置正常 Cookie 并执行正常环境]
O -->|匹配到灰度路由| R[执行灰度路由环境]
O -->|匹配到灰度 IP 正则| S{请求 IP 是否匹配正则}
S -->|匹配| T[设置灰度 Cookie 并执行灰度环境]
S -->|不匹配| U[设置正常 Cookie 并执行正常环境]
N -->|不存在| V{原灰度缓存是否存在}
V -->|存在灰度用户| W[设置灰度 Cookie 并执行灰度环境]
V -->|存在正常用户| X[设置正常 Cookie 并执行正常环境]
V -->|存在灰度路由| Y[执行灰度路由环境]
V -->|存在灰度 IP 正则| Z{请求 IP 是否匹配正则}
Z -->|匹配| AA[设置灰度 Cookie 并执行灰度环境]
Z -->|不匹配| AB[设置正常 Cookie 并执行正常环境]
V -->|不存在| AC[执行默认环境]
AC --> AD[结束]
