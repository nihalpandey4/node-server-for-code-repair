const fs = require("fs");
const execute_shell = require("../../utilities/execute_shell");

module.exports = async(code,test_cases,class_name)=>{
    return new Promise(async(resolve,reject)=>{
        try{
            //className should be same as fileName
            fs.writeFileSync(`resources/${class_name}.java`,code);
            fs.writeFileSync(`resources/${class_name}Test.java`,test_cases);
            const initiate_command = `java -jar kGenProg.jar -r ./ -s ./resources/${class_name}.java -t ./resources/${class_name}Test.java --patch-output -o ./output`
            const response = await execute_shell(initiate_command);
            console.log(response);
            output_code = fs.readFileSync("output/resources.Codefile.java","utf8");  
            resolve(output_code);
        }
        catch(err){
            reject(err);
        }
    })
}