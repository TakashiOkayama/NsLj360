const express = require("express");
const bodyParser = require("body-parser");
const mongoose = require('mongoose');
const Pusher = require("pusher");

// API key情報は環境に合わせて設定する
const pusher = new Pusher({
  appId: "xxxxxxx",
  key: "xxxxxxxxxxxxxxxxxxx",
  secret: "xxxxxxxxxxxxxxxxxxx",
  cluster: "xxx",
});
const app = express();

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

mongoose.connect('mongodb://127.0.0.1/db');

const Schema = mongoose.Schema;
const userSchema = new Schema({
  name: { type: String, required: true },
  count: { type: Number }
});

// ユーザー作成時にフックして番号（count）を追加する
userSchema.pre('save', function(next) {
  if (this.isNew) {
    User.count().then(res => {
      this.count = res; // Increment count
      next();
    });
  } else {
    next();
  }
});
var User = mongoose.model('User', userSchema);

module.exports = User;

var currentUser;

app.post('/login', (req,res) => {
  console.log('called /login');
  User.findOne({name: req.body.name}, (err, user) => {
    if (err) {
      res.send("Error connecting to database");
    }
    
    // User exists
    if (user) {
      console.log(JSON.stringify(user))
      currentUser = user;
      return res.status(200).send(user)
    }
    
    let newuser = new User({name: req.body.name});

    newuser.save(function(err) {
      // console.log(err);
      if (err) throw err;
    });
    
    currentUser = newuser;
    res.status(200).send(newuser)
  });
})

app.get('/users', (req, res) => {
  console.log('called /users');
  User.find({}, (err, users) => {
    if (err) throw err;
    console.log(users);
    res.send(users);
  });
})

app.post('/pusher/auth/presence', (req, res) => {
  console.log('called /pusher/auth/presence');
  console.log(`socket_id: ${req.body.socket_id}`)
  console.log(`channel_name: ${req.body.channel_name}`)
  let socketId = req.body.socket_id;
  let channel = req.body.channel_name;
  
  console.log(currentUser)
  let presenceData = {
    user_id: currentUser._id,
    user_info: {count: currentUser.count, name: currentUser.name}
  };
  
  let auth = pusher.authenticate(socketId, channel, presenceData);

  console.log(auth);
  res.send(auth);
});

app.post('/pusher/auth/private', (req, res) => {
  console.log('called /pusher/auth/private');
  console.log(`socket_id: ${req.body.socket_id}`)
  console.log(`channel_name: ${req.body.channel_name}`)
  res.send(pusher.authenticate(req.body.socket_id, req.body.channel_name));
});

app.post('/send-message', (req, res) => {
  console.log('called /send-message');
  let payload = {message: req.body.message, sender_id: req.body.sender_id}
  pusher.trigger(req.body.channel_name, 'new-message', payload);
  res.status(200).send({message: 'OK'});
});

const port = process.env.PORT || 5000;
app.listen(port);

