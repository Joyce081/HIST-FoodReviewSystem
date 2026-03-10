declare module '@/utils/validate' {
  /**
   * 判断路径是否为外链
   * @param path 待验证的路径字符串
   * @returns 是否为外链
   */
  export function isExternal(path: string): boolean;

  /**
   * 判断字符串是否为空（包括 null/undefined/空字符串）
   * @param value 待检测的值
   * @returns 是否为空
   */
  export function isEmpty(value: any): boolean;

  /**
   * 验证用户名有效性（示例：仅允许 'admin' 或 'editor'）
   * @param str 用户名
   * @returns 是否有效
   */
  export function validUsername(str: string): boolean;

  /**
   * 验证 URL 格式合法性
   * @param url 待验证的 URL
   * @returns 是否合法
   */
  export function validURL(url: string): boolean;

  /**
   * 验证字符串是否为全小写
   * @param str 待验证字符串
   * @returns 是否全小写
   */
  export function validLowerCase(str: string): boolean;

  /**
   * 验证字符串是否为全大写
   * @param str 待验证字符串
   * @returns 是否全大写
   */
  export function validUpperCase(str: string): boolean;

  /**
   * 验证字符串是否为纯字母
   * @param str 待验证字符串
   * @returns 是否纯字母
   */
  export function validAlphabets(str: string): boolean;

  /**
   * 验证邮箱格式合法性
   * @param email 邮箱地址
   * @returns 是否合法
   */
  export function validEmail(email: string): boolean;

  /**
   * 判断值是否为字符串类型
   * @param str 待检测值
   * @returns 是否为字符串
   */
  export function isString(str: any): boolean;

  /**
   * 判断值是否为数组类型
   * @param arg 待检测值
   * @returns 是否为数组
   */
  export function isArray(arg: any): boolean;
}
