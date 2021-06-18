const fs = require("fs");
const execute_shell = require("../../utilities/execute_shell");

module.exports = async(code,test_cases,class_name)=>{
    return new Promise(async(resolve,reject)=>{
        try{
            //className should be same as fileName
            fs.writeFileSync(`kGenProg/example/repair_code_eg/src/example/${class_name}.java`,code);
            fs.writeFileSync(`kGenProg/example/repair_code_eg/src/example/${class_name}Test.java`,test_cases);
            const initiate_command =`java -jar kGenProg.jar -r ./ -s kGenProg/example/repair_code_eg/src/example/${class_name}.java -t kGenProg/example/repair_code_eg/src/example/${class_name}Test.java --patch-output -o ./output`
            const response = await execute_shell(initiate_command);
            console.log(response);
            output_code = fs.readFileSync(`output/patch-v7/example.${class_name}.java`,"utf8");  
            resolve(output_code);
        }
        catch(err){
            reject(err);
        }
    })
}

//java -jar kGenProg.jar -r ./ -s kGenProg/example/repair_code_eg/CloseToZero.java -t kGenProg/example/repair_code_eg/CloseToZeroTest.java --patch-output -o ./output