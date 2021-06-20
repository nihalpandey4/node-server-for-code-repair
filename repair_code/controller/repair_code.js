const repair_code = require("../service/repair_code");

module.exports = async(req,res)=>{
    try{
        const code = req.body.code;
        const test_cases = req.body.test_cases;
        const class_name = req.body.class_name;
        const response= await repair_code(code,test_cases,class_name);
        res.status(200).json({
            message:"success",
            corrected_code:response.output_code,
            logs:response.command_response
        });
    }
    catch(err){
        res.status(200).json({
            message:err.message,
            error:"Unable to fix it!",
            logs:err.message
        });
    }
}