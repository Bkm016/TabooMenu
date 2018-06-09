# TabooMenu
根据 ChestCommands 框架开发而成，一拳一个 dm 怪。

---
### 插件功能

+ 菜单中所有节点均忽略大小写判断。
+ 更先进的界面自动刷新方式。
+ 删除 open-with-item 节点，推荐使用 CustomJoinItem 或 BaseItem 当做菜单物品。
+ 使用 ChestCommands 格式载入，复制文件便可直接使用。
+ 保留 ChestCommands 物品配置中的大部分节点。
+ 保留 ChestCommands 的 51 项物品别名。
+ 保留 ChestCommands 的 cc 命令别名，直接替换也不会影响其他插件打开菜单。
+ 更简单的物品位置写法并保留 POSITION-X/Y 节点。
+ 更简单的损伤值写法并保留 DATA-VALUE 节点。
+ 更简单的附魔写法并保留 ENCHANTMENT 节点。
+ 更强大的指令写法并保留 COMMAND 节点。
+ 新增 ATTRIBUTE-HIDE 节点用于决定是否隐藏属性。
+ 新增 SLOT-COPY 节点用于复制该物品到其他位置。
+ 新增 SHINY 节点用于决定是否显示附魔特效。
+ 新增 REQUIREMENT 节点并保留 PRICE, POINTS, LEVEL 节点。
+ 新增 PERMISSION-BYPASS 节点用于跳过页面的权限判断。
+ 强过 DeluxeMenu 无数倍的条件表达式写法。
+ 物品 ID 容错。
+ 7 种命令触发方式。
+ 11 种命令执行方式。
+ PlaceholderAPI 支持。
+ ...

---
### 创建菜单
```yaml
在路径 /plugins/TabooMenu/menu/ 内创建任意以 yml 结尾编码为 UTF-8 的文件。
```

### 菜单设置
```yaml
# 菜单设置节点名
menu-settings:
  # 菜单名
  name: '测试菜单'
  # 菜单行数
  rows: 1
  # 菜单打开所需要的命令，使用 ";" 符号区分
  command: 'menu;help'
  # 菜单自动刷新时间，单位：秒
  auto-refresh: 1
  # 菜单打开执行动作
  open-action: 'sound: BLOCK_CHEST_OPEN-1-1'
  # 菜单打开是否跳过权限判断
  permission-bypass: false
```

### 物品设置
```yaml
# 物品位置，如果该名称非数字，则需要使用 POSITION-X/Y 节点。
0:
  # 物品名称，如果你不知道你要使用的物品名称，你可以写上印象中大概的英文名。
  # TabooMenu 会为你自动联想最相似的物品。
  id: dixxxx sword
  # 物品名称
  name: '&b钻石剑'
  # 物品描述
  lore:
  - ''
  - '&7使用 &e100 &7点券购买'
  # 物品命令
  command: 'console: points take %player_name% 100;console: give %player_name% diamond_sword 1'
  # 物品条件
  requirement:
  # 物品条件表达式
  - expression: '%playerpoints_points% > 100'
    # 物品条件显示物品（如果条件达成则显示该物品）
    item:
      id: barrier
      name: '&b钻石剑'
      lore:
      - ''
      - '&7使用 &e100 &7点券购买'
      - '&4你的点券不足'
      command: 'tell: &7你的点券不足无法购买'
```

不想写了，突然懒惰。

---
### 声明

本插件部分代码来自 **filoghost** 的开源项目 **ChestCommands**。
