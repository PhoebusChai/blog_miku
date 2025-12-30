import { ElMessage } from 'element-plus'
import type { MessageOptions } from 'element-plus'

/**
 * 消息提示工具类
 */
export const message = {
  /**
   * 成功提示
   */
  success(msg: string, options?: Partial<MessageOptions>) {
    ElMessage.success({
      message: msg,
      duration: 3000,
      showClose: true,
      ...options
    })
  },

  /**
   * 错误提示
   */
  error(msg: string, options?: Partial<MessageOptions>) {
    ElMessage.error({
      message: msg,
      duration: 3000,
      showClose: true,
      ...options
    })
  },

  /**
   * 警告提示
   */
  warning(msg: string, options?: Partial<MessageOptions>) {
    ElMessage.warning({
      message: msg,
      duration: 3000,
      showClose: true,
      ...options
    })
  },

  /**
   * 信息提示
   */
  info(msg: string, options?: Partial<MessageOptions>) {
    ElMessage.info({
      message: msg,
      duration: 3000,
      showClose: true,
      ...options
    })
  }
}

export default message

// 别名导出，兼容不同的导入方式
export const showMessage = message
