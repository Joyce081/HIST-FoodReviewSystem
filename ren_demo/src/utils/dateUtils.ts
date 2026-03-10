// src/utils/time.ts

/**
 * 时间格式化工具类
 * 支持秒级时间戳转换为多种日期时间格式
 */
export class TimeFormatter {
  /**
   * 将秒时间戳转换为指定格式的日期字符串
   * @param timestamp 秒时间戳
   * @param pattern 格式化模式（默认为'YYYY-MM-DD HH:mm:ss'）
   * @returns 格式化后的日期时间字符串
   */
  static format(
    timestamp: number,
    pattern: string = 'YYYY-MM-DD HH:mm:ss'
  ): string {
    const date = new Date(timestamp * 1000); // 转换为毫秒

    // 扩展Date原型以支持格式化方法
    const pad = (n: number) => n.toString().padStart(2, '0');

    const formats: Record<string, () => string | number> = {
      // 完整日期时间
      'YYYY': () => date.getFullYear(),
      'YY': () => date.getFullYear().toString().slice(-2),
      'MM': () => pad(date.getMonth() + 1),
      'M': () => date.getMonth() + 1,
      'DD': () => pad(date.getDate()),
      'D': () => date.getDate(),
      'HH': () => pad(date.getHours()),
      'H': () => date.getHours(),
      'hh': () => pad(date.getHours() % 12 || 12),
      'h': () => date.getHours() % 12 || 12,
      'mm': () => pad(date.getMinutes()),
      'm': () => date.getMinutes(),
      'ss': () => pad(date.getSeconds()),
      's': () => date.getSeconds(),
      'SSS': () => pad(date.getMilliseconds()).padEnd(3, '0'),
      'SS': () => pad(date.getMilliseconds()).slice(0, 2),
      'S': () => Math.floor(date.getMilliseconds() / 100),
      'A': () => date.getHours() < 12 ? 'AM' : 'PM',
      'a': () => date.getHours() < 12 ? 'am' : 'pm',
      // 常用组合格式
      'YYYY-MM-DD': () => `${formats['YYYY']()}-${formats['MM']()}-${formats['DD']()}`,
      'MM-DD': () => `${formats['MM']()}-${formats['DD']()}`,
      'HH:mm:ss': () => `${formats['HH']()}:${formats['mm']()}:${formats['ss']()}`,
      'HH:mm': () => `${formats['HH']()}:${formats['mm']()}`,
    };

    // 特殊格式直接返回
    if (formats[pattern]) return formats[pattern]() as string;

    // 处理自定义格式
    return pattern.replace(/(YYYY|YY|MM|M|DD|D|HH|H|hh|h|mm|m|ss|s|SSS|SS|S|A|a)/g,
      match => formats[match] ? formats[match]().toString() : match
    );
  }

  /**
   * 转换为相对时间（例如："3分钟前"）
   * @param timestamp 秒时间戳
   * @returns 相对时间描述
   */
  static relativeTime(timestamp: number): string {
    const now = Math.floor(Date.now() / 1000);
    const diff = now - timestamp;

    if (diff < 0) return '将来';

    const intervals = [
      { seconds: 31536000, label: '年' },
      { seconds: 2592000, label: '个月' },
      { seconds: 86400, label: '天' },
      { seconds: 3600, label: '小时' },
      { seconds: 60, label: '分钟' },
      { seconds: 1, label: '秒' }
    ];

    for (const interval of intervals) {
      const count = Math.floor(diff / interval.seconds);
      if (count >= 1) {
        return `${count}${interval.label}前`;
      }
    }

    return '刚刚';
  }

  /**
   * 转换为中文格式日期
   * @param timestamp 秒时间戳
   * @param showTime 是否显示时间
   * @returns 中文格式日期
   */
  static chineseFormat(timestamp: number, showTime: boolean = false): string {
    const date = new Date(timestamp * 1000);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();

    let result = `${year}年${month}月${day}日`;

    if (showTime) {
      const hour = date.getHours();
      const minute = date.getMinutes();
      result += ` ${hour}时${minute}分`;
    }

    return result;
  }

  /**
   * 转换为IOS标准格式（YYYY-MM-DDTHH:mm:ssZ）
   * @param timestamp 秒时间戳
   * @returns ISO格式字符串
   */
  static isoFormat(timestamp: number): string {
    return new Date(timestamp * 1000).toISOString();
  }

  /**
   * 获取时间段的友好表示
   * @param start 开始时间戳（秒）
   * @param end 结束时间戳（秒）
   * @returns 时间段描述
   */
  static durationFormat(start: number, end: number): string {
    const diff = end - start;

    if (diff < 0) return '时间段无效';

    const hours = Math.floor(diff / 3600);
    const minutes = Math.floor((diff % 3600) / 60);
    const seconds = diff % 60;

    const parts = [];
    if (hours > 0) parts.push(`${hours}小时`);
    if (minutes > 0) parts.push(`${minutes}分钟`);
    if (seconds > 0) parts.push(`${seconds}秒`);

    return parts.join('');
  }

  /**
   * 获取当天开始/结束时间
   * @param timestamp 秒时间戳
   * @param type 返回类型：'start' 返回当天00:00:00, 'end' 返回当天23:59:59
   * @returns 秒时间戳
   */
  static dayBoundary(
    timestamp: number,
    type: 'start' | 'end' = 'start'
  ): number {
    const date = new Date(timestamp * 1000);
    date.setHours(0, 0, 0, 0);
    const start = Math.floor(date.getTime() / 1000);

    if (type === 'start') return start;

    return start + 86399; // 24小时-1秒
  }
}

/**
 * 导出常用函数快捷方式
 * 按需使用具体方法或快捷方式
 */

// 默认导出格式化函数
export function formatTime(
  timestamp: number,
  pattern: string = 'YYYY-MM-DD HH:mm:ss'
): string {
  return TimeFormatter.format(timestamp, pattern);
}

// 快捷导出常用格式
export const formatToDateTime = (timestamp: number) =>
  TimeFormatter.format(timestamp, 'YYYY-MM-DD HH:mm:ss');

export const formatToDate = (timestamp: number) =>
  TimeFormatter.format(timestamp, 'YYYY-MM-DD');

export const formatToTime = (timestamp: number) =>
  TimeFormatter.format(timestamp, 'HH:mm:ss');

export const relativeTime = (timestamp: number) =>
  TimeFormatter.relativeTime(timestamp);

export const isoFormatTime = (timestamp: number) =>
  TimeFormatter.isoFormat(timestamp);
