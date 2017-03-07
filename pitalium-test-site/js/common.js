(function () {
  'use strict';

  var HTMLElement = window.HTMLElement || window.Element;
  var ratio = window.devicePixelRatio || 1;
  window.getPixelRatio = function () {
    return ratio;
  };

  /**
   * HTML要素にCSSクラスを追加します。
   *
   * @param {string} name 追加するクラス名
   */
  HTMLElement.prototype.addClassName = function (name) {
    // Modern browser implements
    if ('classList' in this) {
      this.classList.add(name);
      return;
    }

    // <= IE10
    var classes = this.className.split(' ');
    for (var i = 0; i < classes.length; i++) {
      if (classes[i] === name) {
        return;
      }
    }
    classes.push(name);
    this.className = classes.join(' ');
  };

  /**
   * HTML要素の座標と大きさを実際の表示ピクセルで取得します。
   *
   * @return {{x: number, y: number, width: number, height: number}}
   */
  HTMLElement.prototype.getPixelRect = function () {
    var rect = this.getBoundingClientRect();
    return {
      x: rect.left * ratio,
      y: rect.top * ratio,
      width: rect.width * ratio,
      height: rect.height * ratio
    }
  };

  /**
   * HTML要素の大きさを実際に表示ピクセルで取得します。
   *
   * @return {{width: number, height: number}}
   */
  HTMLElement.prototype.getPixelSize = function () {
    var rect = this.getBoundingClientRect();
    return {
      width: rect.width * ratio,
      height: rect.height * ratio
    }
  };

  /**
   * HTML要素のインラインCSSスタイル一覧を取得します。
   *
   * @return {Object}
   */
  HTMLElement.prototype.getInlineStyles = function () {
    var result = {};
    var style = this.style;
    for (var name in style) {
      var value = style[name];
      if (typeof value === 'string' && value !== '') {
        result[name] = value;
      }
    }
    return result;
  };

  /**
   * QueryStringの値を取得します。
   *
   * @return {{get: Function<string, string, string>}}
   */
  window.getQueryString = function () {
    var query = window.location.search;
    var values = {};
    var result = {
      get: function (key, defaultValue) {
        return key in values ? values[key] : defaultValue;
      }
    };
    if (!query.length) {
      return result;
    }

    var hash = query.slice(1).split('&');
    for (var i = 0; i < hash.length; i++) {
      var val = hash[i].split('=');
      values[val[0]] = val[1];
    }
    return result;
  };

  /**
   * 指定したHTML要素の内部に1つ20pxの縦横両方向グラデーションで塗りつぶします。
   *
   * @param {HTMLElement} container 塗りつぶすHTML要素
   * @param {number} [blue=0xff] 固定青色
   */
  window.fillGradation = function (container, blue) {
    blue = blue === undefined ? 0xff : blue;
    var rect = container.getBoundingClientRect();

    function incrementColor(color) {
      color += 8;
      return color <= 0xff ? color : 0;
    }

    var red = 0;
    var green = 0;
    var x = 0;
    var y = 0;
    var maxX = rect.width;
    var maxY = rect.height;
    var row = null;
    while (y < maxY) {
      row = document.createElement('div');
      row.className = 'gradation-row';
      row.style.top = y + 'px';

      while (x < maxX) {
        var color = 'rgb(' + red + ',' + green + ',' + blue + ')';
        var element = document.createElement('div');
        element.className = 'gradation-column';
        element.style.backgroundColor = color;
        element.style.left = x + 'px';
        row.appendChild(element);
        red = incrementColor(red);

        x += 20;
      }
      container.appendChild(row);

      x = 0;
      y += 20;
      red = 0;
      green = incrementColor(green);
    }
  };

  /**
   * 日付を &quot;yyyy/MM/dd hh:mm:ss.SSS&quot; 形式で返します。
   *
   * @param {Date} [date]
   * @returns {string}
   */
  window.getFormatDate = function (date) {
    date = date || new Date();
    var year = zeroFill(date.getFullYear(), 4);
    var month = zeroFill(date.getMonth() + 1, 2);
    var day = zeroFill(date.getDate(), 2);
    var hour = zeroFill(date.getHours(), 2);
    var minute = zeroFill(date.getMinutes(), 2);
    var second = zeroFill(date.getSeconds(), 2);
    var millis = zeroFill(date.getMilliseconds(), 3);
    return year + '/' + month + '/' + day + ' ' + hour + ':' + minute + ':' + second + '.' + millis;
  };

  /**
   * @param {number|string} value
   * @param {number} digits
   * @returns {string}
   */
  function zeroFill(value, digits) {
    var val = value ? value.toString() : '';
    digits = digits || 0;
    return val.length > digits ? val : (new Array(digits + 1).join('0') + val).slice(-digits);
  }

})();