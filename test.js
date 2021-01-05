exports.header = {
    contents: function (pageNum, numPages) {
      return '<div style="'+
      'height: 100%;'+ 
      'width: 100%;' +
      'position: absolute;' +
      'right:.03rem;'+
      'bottom: .03rem;' + 
      'background: black;"/>'
    },
    height: '1cm'
  }
  
  exports.footer = {
    contents: function (pageNum, numPages) {
      return '<div style="'+
                        'height: 100%;'+ 
                        'width: 100%;' +
                        'position: absolute;' +
                        'right:.03rem;'+
                        'bottom: .03rem;' + 
                        'background: black;">' + 
                '<span style="'+
                    'float:right;'+
                    'background: black;'+ 
                    'padding: .5rem;'+
                    'color: rgb(116, 116, 116);">' + pageNum + '</span> </div>'
    },
    height: '1cm'
  }