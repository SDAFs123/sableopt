# SableOpt

Sable Optimizations - 一系列针对 Sable 和 Voxy 的原创优化和修复。

## 功能

### 1. PoseStack Bug 修复
- 修复 Sable EntityRenderDispatcher 的 pop/push 不平衡问题
- 防止 Toast 渲染时崩溃

### 2. 网络优化
- **DeltaSync** - 增量同步（只发送变化的数据）
- **PrecisionCompressor** - 半浮点精度压缩
- **DistanceDecaySync** - 距离衰减同步（远距离降低同步频率）
- **ClientInterpolationMixin** - 客户端插值优化

### 3. 物理优化
- **CollisionCache** - 碰撞缓存
- **OctreeUpdateQueue** - 八叉树更新队列
- **BlockUpdateBatcher** - 方块更新批处理
- **SubLevelPhysicsPool** - SubLevel 物理池（并行处理）

### 4. 渲染优化
- **PoseUpdateBatcher** - Pose 更新批处理
- **SubLevelRenderCache** - SubLevel 渲染缓存
- **SubLevelPoseBatcher** - SubLevel Pose 批处理

### 5. FakeSight Shader 修复
- Shader 加载修复
- requestDistance 配置（调整视距）

### 6. Voxy + Sable 兼容
- **VoxyDetector** - Voxy 模组检测
- **SubLevelLODRenderer** - SubLevel LOD 渲染器
- **SubLevelLODBuilder** - LOD 网格构建器
- **DynamicLODUpdater** - 动态 LOD 更新

## 依赖

- Minecraft 1.21.1
- NeoForge 21.1.226+
- Sable（可选，大部分功能需要）
- Voxy（可选，LOD 兼容功能需要）
- Sodium（配置界面）

## 构建

```bash
./gradlew build
```

输出文件：`build/libs/sableopt-1.0.0.jar`

## 许可证

MIT License