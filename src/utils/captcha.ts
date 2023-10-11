class Captcha {
  private ref: HTMLCanvasElement | null = null;
  private result: number | null = null;
  private height: number;
  private width: number;

  constructor(height: number = 34, width: number = 120) {
    this.height = height;
    this.width = width;
  }

  setRef = (ref: HTMLCanvasElement) => {
    this.ref = ref;
  };

  private randomNum = (min: number, max: number) => {
    return parseInt(String(Math.random() * (max - min) + min));
  };

  private randomColor = (min: number, max: number) => {
    const r = this.randomNum(min, max);
    const g = this.randomNum(min, max);
    const b = this.randomNum(min, max);
    return `rgb(${r},${g},${b})`;
  };

  private randomOperator = () => {
    const operators = ["+", "-", "*"];
    return operators[this.randomNum(0, operators.length)];
  };

  private generateNumbers = () => {
    let num1: number, num2: number;
    num1 = this.randomNum(1, 9);
    num2 = this.randomNum(1, 9);
    if (this.randomNum(0, 2) === 0) {
      [num1, num2] = [num2, num1];
    }
    return [num1, num2];
  };

  private calculateResult = (num1: number, num2: number, operator: string) => {
    let result: number;
    switch (operator) {
      case "+":
        result = num1 + num2;
        break;
      case "-":
        result = num1 - num2;
        break;
      case "*":
        result = num1 * num2;
        break;
      default:
        result = 0;
        break;
    }
    return result;
  };

  public generateCaptcha = () => {
    const canvas = this.ref;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    const [num1, num2] = this.generateNumbers();
    const operator = this.randomOperator();
    const result = this.calculateResult(num1, num2, operator);
    ctx.fillStyle = this.randomColor(180, 255);
    ctx.fillRect(0, 0, this.width, this.height);
    ctx.font = `${this.height / 1.5}px Arial`;
    ctx.textAlign = "center";
    ctx.fillStyle = this.randomColor(0, 100);
    const text = [num1.toString(), operator, num2.toString()];
    for (let i = 0; i < 3; i++) {
      const fontSize = this.randomNum(18, 24);
      const deg = this.randomNum(-15, 18);
      ctx.font = fontSize + "px Simhei";
      ctx.textBaseline = "top";
      ctx.fillStyle = this.randomColor(80, 150);
      ctx.save();
      ctx.translate(30 * i + 15, 15);
      ctx.rotate((deg * Math.PI) / 180);
      ctx.fillText(text[i], 0, 0);
      ctx.restore();
    }
    for (let i = 0; i < this.randomNum(2, 4); i++) {
      ctx.beginPath();
      ctx.moveTo(this.randomNum(0, this.width), this.randomNum(0, this.height));
      ctx.lineTo(this.randomNum(0, this.width), this.randomNum(0, this.height));
      ctx.strokeStyle = this.randomColor(180, 230);
      ctx.closePath();
      ctx.stroke();
    }
    for (let i = 0; i < 35; i++) {
      ctx.beginPath();
      ctx.arc(
        this.randomNum(0, this.width),
        this.randomNum(0, this.height),
        1,
        0,
        2 * Math.PI
      );
      ctx.closePath();
      ctx.fillStyle = this.randomColor(150, 200);
      ctx.fill();
    }
    this.result = result;
  };

  public verify = (value: number) => {
    return value === this.result;
  };
}
export default Captcha;
