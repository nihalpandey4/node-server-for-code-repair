module.exports = (cmd) => {
  const exec = require("child_process").exec;
  return new Promise((resolve, reject) => {
    try {
      exec(cmd, { maxBuffer: 1024 * 500 }, (error, stdout, stderr) => {
        if (error) {
          console.warn(error);
        } else if (stdout) {
          console.log(stdout);
        } else {
          console.log(stderr);
        }
        resolve(stdout ? stdout : false);
      });
    } catch (err) {
      reject(err);
    }
  });
};
