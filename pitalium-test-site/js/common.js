(function () {
  'use strict';

  var HTMLElement = window.HTMLElement || window.Element;
  var ratio = window.devicePixelRatio || 1;
  window.getPixelRatio = function () {
    return ratio;
  };

  /**
   * @param {string} name
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
  HTMLElement.prototype.getPixelRect = function () {
    var rect = this.getBoundingClientRect();
    return {
      x: rect.left * ratio,
      y: rect.top * ratio,
      width: rect.width * ratio,
      height: rect.height * ratio
    }
  };
  HTMLElement.prototype.getPixelSize = function () {
    var rect = this.getBoundingClientRect();
    return {
      width: rect.width * ratio,
      height: rect.height * ratio
    }
  };
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
   * @returns {{get: Function}} {get: (string, string} => string}
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
   * @param {HTMLElement} container
   * @param {number} [blue]
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