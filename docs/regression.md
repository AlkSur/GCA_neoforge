# GCA NeoForge 1.21.1 回归测试清单

> 基于原 GCA v2.9.0 的 9 项 Carpet 设置功能

## 测试环境
- NeoForge 21.1.209
- Carpet: NeoForged 1.0.8
- Java 21

## 功能测试

### 1. openFakePlayerInventory
- [ ] `/carpet openFakePlayerInventory true`
- [ ] 召唤假玩家 `/player bot spawn test`
- [ ] **右键假玩家** → 应打开假玩家背包界面
- [ ] 验证可放入/取出物品

### 2. openFakePlayerEnderChest
- [ ] `/carpet openFakePlayerEnderChest true`
- [ ] 召唤假玩家 `/player bot spawn test2`
- [ ] **蹲下+右键假玩家** → 应打开假玩家末影箱
- [ ] 验证物品存储正确

### 3. fakePlayerResident
- [ ] `/carpet fakePlayerResident true`
- [ ] 召唤3个假玩家
- [ ] 重启服务器
- [ ] 假玩家应自动重新生成
- [ ] 检查 `world/fake_player.gca.json` 文件是否存在

### 4. fakePlayerAutoReplenishment
- [ ] `/carpet fakePlayerAutoReplenishment true`
- [ ] 给假玩家工具（即将耗尽的）
- [ ] 在假玩家背包放入替换工具
- [ ] 假玩家应自动从背包补货

### 5. fakePlayerAutoFish
- [ ] `/carpet fakePlayerAutoFish true`
- [ ] 给假玩家钓鱼竿
- [ ] 假玩家应在水边自动钓鱼
- [ ] 验证鱼获进入背包

### 6. fakePlayerAutoReplaceTool
- [ ] `/carpet fakePlayerAutoReplaceTool true`
- [ ] 给假玩家即将损坏的工具 + 背包里放新工具
- [ ] 假玩家应自动更换工具

### 7. betterFenceGatePlacement
- [ ] `/carpet betterFenceGatePlacement true`
- [ ] 在已有栅栏门上放置栅栏门
- [ ] 新栅栏门应继承原有栅栏门的开关状态

### 8. betterWoodStrip
- [ ] `/carpet betterWoodStrip true`
- [ ] 使用名称不含 "Strip" 的斧头右键木头 → 不应剥皮
- [ ] 使用名称含 "Strip" 的斧头右键木头 → 应正常剥皮

### 9. betterSignInteraction / betterSignEditing
- [ ] `/carpet betterSignInteraction true`
- [ ] `/carpet betterSignEditing true`
- [ ] 右键告示牌应可直接编辑
- [ ] 使用羽毛右键告示牌应可编辑

## 非功能验证
- [ ] 服务端启动日志中 21 个 Mixin 全 applied
- [ ] `/carpet` 菜单中显示 GCA 分类
- [ ] 中文/英文切换正常
- [ ] 服务端单独启动无客户端崩溃
- [ ] `gradlew build` 成功产出 `gca_neoforge-mc1.21.1-2.9.0.jar`

## 兼容性
- [ ] 从 Fabric 版拷贝 `fake_player.gca.json` 到 NeoForge 世界目录，假玩家可恢复
