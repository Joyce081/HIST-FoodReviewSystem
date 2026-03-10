declare global {
  interface Math {
    easeInOutQuad: (t: number, b: number, c: number, d: number) => number;
  }
}

/**
 * 平滑滚动到页面指定位置
 * @param to - 目标滚动位置（像素）
 * @param duration - 动画持续时间（毫秒），默认500ms
 * @param callback - 动画结束后的回调函数
 */
export function scrollTo(
  to: number,
  duration?: number,
  callback?: () => void
): void;

// 声明内部使用的工具函数（非导出）
declare function move(amount: number): void;
declare function position(): number;
declare const requestAnimFrame: (callback: FrameRequestCallback) => number;
