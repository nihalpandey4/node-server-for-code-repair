const repair_code = require("./controller/repair_code");

module.exports = (app)=>{
    app.post("/submit_code_and_test_cases",repair_code);
}