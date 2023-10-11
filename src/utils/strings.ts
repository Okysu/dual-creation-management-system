export function describeTime(date: Date): string {
  const now = new Date();
  const diff = Math.round((now.getTime() - date.getTime()) / 1000);
  if (diff > 0) {
    if (diff < 60) {
      return `${diff}秒前`;
    } else if (diff < 60 * 60) {
      return `${Math.floor(diff / 60)}分钟前`;
    } else if (diff < 60 * 60 * 24) {
      return `${Math.floor(diff / 60 / 60)}小时前`;
    } else if (diff < 60 * 60 * 24 * 2) {
      return "昨天";
    } else if (diff < 60 * 60 * 24 * 3) {
      return "前天";
    } else if (diff < 60 * 60 * 24 * 7) {
      return `${Math.floor(diff / 60 / 60 / 24)}天前`;
    }
    return date.toLocaleString();
  } else {
    const diffFuture = Math.abs(diff);
    if (diffFuture < 60) {
      return `${diffFuture}秒后`;
    } else if (diffFuture < 60 * 60) {
      return `${Math.floor(diffFuture / 60)}分钟后`;
    } else if (diffFuture < 60 * 60 * 24) {
      return `${Math.floor(diffFuture / 60 / 60)}小时后`;
    } else if (diffFuture < 60 * 60 * 24 * 2) {
      return "明天";
    } else if (diffFuture < 60 * 60 * 24 * 3) {
      return "后天";
    } else if (diffFuture < 60 * 60 * 24 * 7) {
      return `${Math.floor(diffFuture / 60 / 60 / 24)}天后`;
    }
    return date.toLocaleString();
  }
}


export const copyText = (str: string): Promise<void> => {
  return navigator.clipboard.writeText(str);
};

export const binarySize = (size: number): string => {
  if (size < 1024) {
    return `${size}B`;
  }
  if (size < 1048576) {
    return `${(size / 1024).toFixed(2)}KB`;
  }
  if (size < 1073741824) {
    return `${(size / 1048576).toFixed(2)}MB`;
  }
  return `${(size / 1073741824).toFixed(2)}GB`;
};
