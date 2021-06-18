const express = require("express");
const repair_code = require("./repair_code");

const app = express();

//json request parsing 
app.use(express.json());

repair_code(app);

app.listen(3000,()=>{
    console.log("server is running at port 3000");
})