var express = require('express');
var app = express();
 
// ser port
app.set('port', 3000);
 
app.use(express.static('public'));
 
app.listen(app.get('port'), function() {
  console.log('Web Server started!!');
});
