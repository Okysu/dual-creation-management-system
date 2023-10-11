/**
 * 防抖函数
 * @param fn 需要防抖的函数
 * @param delay 延时时间 默认500ms
 * @param immediate 是否立即执行 默认false
 * @returns 防抖后的函数
 * @description 防抖函数的作用是在一定时间内，只让最后一次的事件生效
 */
function debounce(
  fn: Function,
  delay: number = 500,
  immediate: boolean = false
) {
  let timerId: NodeJS.Timeout | number;
  return function (...args: any[]) {
    if (timerId) clearTimeout(timerId);
    if (immediate) {
      fn(...args);
      timerId = setTimeout(() => {
        timerId = 0;
      }, delay);
    } else {
      timerId = setTimeout(() => {
        fn(...args);
      }, delay);
    }
  };
}

/**
 * 节流函数
 * @param fn 需要节流的函数
 * @param delay 延时时间 默认500ms
 * @returns 节流后的函数
 * @description 节流函数的作用是在一定时间内，只让第一次的事件生效
 */
function throttle(fn: Function, delay: number = 500) {
  let timerId: NodeJS.Timeout | number;
  return function (...args: any[]) {
    if (timerId) return;
    timerId = setTimeout(() => {
      fn(...args);
      timerId = 0;
    }, delay);
  };
}

/**
 * 深拷贝
 * @param obj 需要拷贝的对象
 * @returns 拷贝后的对象
 */
function deepClone(obj: any) {
  if (obj === null) return null;
  if (typeof obj !== "object") return obj;
  if (obj.constructor === Date) return new Date(obj);
  if (obj.constructor === RegExp) return new RegExp(obj);
  const newObj = new obj.constructor();
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      const val = obj[key];
      newObj[key] = typeof val === "object" ? deepClone(val) : val;
    }
  }
  return newObj;
}

/**
 * 深比较
 * @param obj1 对象1
 * @param obj2 对象2
 * @returns 是否相等
 */
function deepEqual(obj1: any, obj2: any) {
  if (obj1 === obj2) return true;
  if (typeof obj1 !== "object" || typeof obj2 !== "object") return false;
  if (obj1.constructor !== obj2.constructor) return false;
  for (const key in obj1) {
    if (obj1.hasOwnProperty(key)) {
      const val1 = obj1[key];
      const val2 = obj2[key];
      if (!deepEqual(val1, val2)) return false;
    }
  }
  return true;
}

/**
 * 深冻结
 * @param obj 需要冻结的对象
 * @returns 冻结后的对象
 */
function deepFreeze(obj: any) {
  if (obj === null) return null;
  if (typeof obj !== "object") return obj;
  if (obj.constructor === Date) return new Date(obj);
  if (obj.constructor === RegExp) return new RegExp(obj);
  const newObj = new obj.constructor();
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      const val = obj[key];
      newObj[key] = typeof val === "object" ? deepFreeze(val) : val;
    }
  }
  return Object.freeze(newObj);
}

/**
 * 判断宽度是否小于某个值并监听
 * @param width 宽度
 * @parm callback 回调函数
 * @returns 是否小于
 */
function isWidthLessThan(width: number, callback: Function): boolean {
  const listener = debounce(() => {
    callback(window.innerWidth < width);
  }, 300);
  window.addEventListener("resize", listener);
  return window.innerWidth < width;
}

/**
 * 事件发布订阅
 */
interface EventMap {
  [eventName: string]: Function[];
}

/**
 * 事件发布订阅
 */
class EventPubSub {
  private events: EventMap = {};

  subscribe(eventName: string, fn: Function) {
    this.events[eventName] = this.events[eventName] || [];
    this.events[eventName].push(fn);
  }

  publish(eventName: string, data: any) {
    if (this.events[eventName]) {
      this.events[eventName].forEach((fn) => fn(data));
    }
  }
}

export {
  debounce,
  throttle,
  deepClone,
  deepEqual,
  deepFreeze,
  isWidthLessThan,
  EventPubSub,
};
