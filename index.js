const express = require("express");
const repair_code = require("./repair_code");

const cors = require("cors");

const app = express();

app.use(express.json());

app.use(cors({
    origin: "*",
    credentials: true,
  }));

//json request parsing 

repair_code(app);

app.listen(3001,()=>{
    console.log("server is running at port 3001");
})