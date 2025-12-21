# Element Plus Message 组件集成说明

## 已完成的配置

### 1. 安装依赖

```bash
npm install element-plus --save
```

### 2. 引入 Message 组件

在 `src/main.ts` 中按需引入：

```typescript
import { ElMessage } from 'element-plus'
import 'element-plus/es/components/message/style/css'
```

### 3. 创建工具类

创建了 `src/utils/message.ts` 工具类，封装了常用的消息提示方法：

```typescript
import { message } from '@/utils/message'

// 成功提示
message.success('操作成功')

// 错误提示
message.error('操作失败')

// 警告提示
message.warning('请注意')

// 信息提示
message.info('提示信息')
```

### 4. 自定义样式

在 `src/assets/styles/element-plus.css` 中自定义了 Message 组件的样式，使其与博客主题色（初音绿）保持一致。

## 使用方法

### 基础用法

```typescript
import { message } from '@/utils/message'

// 在任何 Vue 组件中使用
message.success('注册成功！')
message.error('邮箱格式不正确')
message.warning('请先输入邮箱地址')
message.info('验证码已发送')
```

### 高级用法

```typescript
import { message } from '@/utils/message'

// 自定义持续时间
message.success('操作成功', { duration: 5000 })

// 可关闭的消息
message.error('错误信息', { showClose: true })

// 自定义偏移量
message.info('提示', { offset: 100 })
```

### 直接使用 ElMessage

如果需要更多自定义选项，也可以直接使用 ElMessage：

```typescript
import { ElMessage } from 'element-plus'

ElMessage({
  message: '自定义消息',
  type: 'success',
  duration: 3000,
  showClose: true,
  center: true,
  offset: 20
})
```

## 在注册页面的应用

已在 `src/views/Register.vue` 中集成：

1. **发送验证码成功**：显示成功提示
2. **发送验证码失败**：显示错误提示
3. **表单验证失败**：显示警告提示
4. **注册成功**：显示成功提示

## 样式定制

Message 组件的样式已根据博客主题进行定制：

- **成功消息**：使用初音绿色系
- **错误消息**：使用红色系
- **警告消息**：使用橙色系
- **信息消息**：使用蓝色系

所有消息都有：
- 圆角边框
- 阴影效果
- 可关闭按钮
- 3秒自动关闭

## 注意事项

1. **按需引入**：只引入了 Message 组件，不会增加太多打包体积
2. **全局可用**：通过工具类封装，在任何组件中都可以方便使用
3. **类型安全**：完整的 TypeScript 类型支持
4. **主题一致**：样式与博客整体设计保持一致

## API 接口集成

已创建 `src/api/email.ts`，包含：

```typescript
// 发送验证码
sendVerificationCode(email: string)
```

已更新 `src/api/auth.ts`，新增：

```typescript
// 使用验证码注册
registerWithCode(data: RegisterWithCodeParams)
```

## 后续扩展

可以继续引入其他 Element Plus 组件：

```typescript
// 按需引入其他组件
import { ElDialog, ElForm, ElInput } from 'element-plus'
import 'element-plus/es/components/dialog/style/css'
import 'element-plus/es/components/form/style/css'
import 'element-plus/es/components/input/style/css'
```

或者全量引入（不推荐，会增加打包体积）：

```typescript
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

app.use(ElementPlus)
```
