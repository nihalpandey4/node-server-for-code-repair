const fs = require("fs");
const execute_shell = require("../../utilities/execute_shell");
const path = require("path");

module.exports = async(code,test_cases,class_name)=>{
    return new Promise(async(resolve,reject)=>{
        try{
            //className should be same as fileName
            //delete the output folder
            const decoded_code = decodeURIComponent(code);
            const decoded_test_cases = decodeURIComponent(test_cases);
            fs.writeFileSync(`kGenProg/example/repair_code_eg/src/example/${class_name}.java`,decoded_code);
            fs.writeFileSync(`kGenProg/example/repair_code_eg/src/example/${class_name}Test.java`,decoded_test_cases);
            const initiate_command =`java -jar kGenProg.jar -r ./ -s kGenProg/example/repair_code_eg/src/example/${class_name}.java -t kGenProg/example/repair_code_eg/src/example/${class_name}Test.java --patch-output -o ./output`
            const command_response = await execute_shell(initiate_command);
            const output_dir = fs.readdirSync(path.join(__dirname,"../../output"));
            output_code = await  fs.readFileSync(`output/${output_dir[0]}/example.${class_name}.java`,"utf8");  
            output_code = output_code.slice(output_code.indexOf("\n")+1);
            resolve({output_code,command_response});
        }
        catch(err){
            reject(err);
        }
    })
}

//java -jar kGenProg.jar -r ./ -s kGenProg/example/repair_code_eg/CloseToZero.java -t kGenProg/example/repair_code_eg/CloseToZeroTest.java --patch-output -o ./output