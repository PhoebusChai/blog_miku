import request from './request'

/**
 * 发送验证码
 */
export function sendVerificationCode(email: string) {
  return request({
    url: '/email/send-code',
    method: 'post',
    params: { email }
  })
}
