function checkTargetOutput(){

    // get the problem number from the url
    var url = String(new URL(window.location.href));
    url = url.split("/")
    id = url[url.length - 2];

    console.log(id)

    switch(id){
        case "1":
            if(result.session.getValue().replace(/\s+$/, '') == "Hello World"){
                message("success", "The output is correct! Problem solved!");
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "2":
            output = result.session.getValue().replace(/\s+$/, '');
            output = output.split(" ");
            if(output[0] == "My" && output[1] == "name" && output[2] == "is" 
                && output[output.length - 4] == "and" && output[output.length - 3] == "I" && output[output.length - 2] == "am"){
                    keys = getMemory().map(a => a.key);
                    if(keys.includes("name") && keys.includes("age")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does not make use of the variables \"name\" and/or \"age\"! Try again!");
                    }
                }
                else{
                    message("error", "The output is incorrect! Try again!");
                }
            break;
        case "3":
            if(result.session.getValue().replace(/\s+$/, '') == "152"){
                keys = getMemory().map(a => a.key);
                if(
                    keys.includes("balance") &&
                    keys.includes("rent") &&
                    keys.includes("coffee") &&
                    keys.includes("payCheck") &&
                    keys.includes("schoolSupplies") &&
                    keys.includes("moneyTransfer")
                ){
                    message("success", "The output is correct! Problem solved!");
                }
                else{
                    message("error", "Your program does not make use of the variables \"balance\", \"rent\", \"coffee\", \"payCheck\", \"schoolSupplies\", and/or \"moneyTransfer\"! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "4":
            output = result.session.getValue().replace(/\s+$/, '');
            code = editor.session.getValue()
            if(output == "x is even" || output == "x is odd"){
                keys = getMemory().map(a => a.key);
                if(keys.includes("x")){
                    if(code.includes("if") && code.includes("otherwise")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does not make use of the if/otherwise statement! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of the variable \"x\"! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "5":
            if(result.session.getValue().replace(/\s+$/, '') == "68.0"){
                memory = getMemory();
                if (memory.some(obj => obj.key === "celsius" && obj.value === "20")){
                    if(memory.map(a => a.key).includes("fahrenheit")){
                        if(editor.session.getValue().includes("mult") && editor.session.getValue().includes("add")){
                            message("success", "The output is correct! Problem solved!");
                        }
                        else{
                            message("error", "Your program does not make use of the mult and/or add operators! Try again!");
                        }
                    }
                    else{
                        message("error", "Your program does not make use of the variable \"fahrenheit\"! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of the variable \"celsius\", or does not initialise it as 20! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "6":
            if(result.session.getValue().replace(/\s+$/, '') == "[1, 2, 3, 4, 5]"){
                memory = getMemory();
                if(memory.map(a => a.key).includes("list")){
                    if(editor.session.getValue().includes("remove") && editor.session.getValue().includes("remove all") && editor.session.getValue().includes("add")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does not make use of the remove, remove all and/or add functions! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of the variable \"list\"! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "7":
            if(result.session.getValue().replace(/\s+$/, '') == "Odd\nOdd\nEven\nEven\nOdd\nEven\nOdd"){
                memory = getMemory();
                if(memory.map(a => a.key).includes("list")){
                    if(editor.session.getValue().includes("loop for each") && editor.session.getValue().includes("if") && editor.session.getValue().includes("otherwise")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does not make use of the loop for each, if and/or otherwise statements! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of the variable \"list\"! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "8":
            if(result.session.getValue().replace(/\s+$/, '') == "10"){
                memory = getMemory();
                if(memory.map(a => a.key).includes("index") && memory.map(a => a.key).includes("target") && memory.map(a => a.key).includes("count")){
                    if(editor.session.getValue().includes("loop") && editor.session.getValue().includes("if")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does not make use of the loop and/or if statement! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of the variables \"index\", \"target\" and/or \"count\"! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "9":
            if(result.session.getValue().replace(/\s+$/, '') == "4\n8"){
                if(editor.session.getValue().includes("function addTwo")){
                    if(editor.session.getValue().includes("addTwo(2, 2)") && editor.session.getValue().includes("addTwo(5, 3)")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does contain the code addTwo(2, 2) and/or addTwo(5, 3)! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of the function addTwo! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        case "10":
            if(result.session.getValue().replace(/\s+$/, '') == "3\n55"){
                if(editor.session.getValue().includes("function fibonacci(n)")){
                    if(editor.session.getValue().includes("fibonacci(4)") && editor.session.getValue().includes("fibonacci(10)")){
                        message("success", "The output is correct! Problem solved!");
                    }
                    else{
                        message("error", "Your program does contain the code fibonacci(4) and/or fibonacci(10)! Try again!");
                    }
                }
                else{
                    message("error", "Your program does not make use of a function fibonacci(n)! Try again!");
                }
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
            break;
        default:
            message("error", "No target output available");
            break;
    }
}

function generateProblem(){
    var targetOutput = ace.edit("targetOutput", {
        theme: "ace/theme/tomorrow_night_eighties",
        mode: "ace/mode/text",
        minLines: 5,
        maxLines: 5,
        wrap: false,
        autoScrollEditorIntoView: true,
        readOnly: true
    });

    targetOutput.setOptions({
        fontSize: "12pt"
    });

    // get the id from the url
    var url = String(new URL(window.location.href));
    url = url.split("/")
    id = url[url.length - 2];

    switch(id){
        case "1":
            document.getElementById("problem").innerHTML = "Objective: Write a program that outputs the message “Hello World” using the built-in write() function."
            targetOutput.session.setValue("Hello World");
            break;
        case "2":
            document.getElementById("problem").innerHTML = "Objective: Write a program that outputs your own name and age in the message: \"My name is <name> and I am <age>\". Make use of the variables \"name\" and \"age\" to store this information."
            targetOutput.session.setValue("My name is <name> and I am <age>");
            break;
        case "3":
            document.getElementById("problem").innerHTML = "Objective: Write a program to mimic the way in which money enters and leaves your bank account. Define your initial variable, balance, to be 100. Then, using each of these variables, update the value of the variable balance. At the end output your final balance. Hint: Payments are subtracted to your balance while incomes are added. Here is a list of 5 transactions to update with:<br><ul><li>rent is 50 (payment)</li><li>coffee is 3 (payment)</li><li>payCheck is 100 (income)</li><li>schoolSupplies is 25 (payment)</li><li>moneyTransfer is 30 (income)</li></ul>Remember you can update a variable like follows:<br><br><code>x is x<br>x is x + 1</code>"
            targetOutput.session.setValue("152");
            break;
        case "4":
            document.getElementById("problem").innerHTML = "Objective: Write a program to determine if a defined variable x, is even or is odd. Make use of an if/otherwise statement to solve this problem. Your output can be one of the following:"
            targetOutput.session.setValue("x is even\n\nOR\n\nx is odd");
            break;
        case "5":
            document.getElementById("problem").innerHTML = "Objective: Write a program that converts temperature from Celsius to Fahrenheit. The steps to convert to Fahrenheit are as follows:<br><ul><li>Take your original number and multiply by 1.8</li><li>Then add 32 to the result</li></ul>Make use of a variables named \"fahrenheit\" and \"celsius\". Celsius should begin at 20. Your program should make use of the \"mult\" and \"add\" operators."
            targetOutput.session.setValue("68.0");
            break;
        case "6":
            document.getElementById("problem").innerHTML = "Objective: Given we start with a list with the following value: [1,2,\"x\",1,2,3,\"x\",4,6,\"x\"]. As we can see, this list has gotten corrupted. We have the list repeating after the first 2 numbers, as well as a 6 instead of 5 at the end of the list. As well as these we have many corrupted x’s throughout. Make a program that removes all of these unwanted characters, and adds 5 correctly to the end of the list. Finally output the corrected list.<br>Hint: Make use of the remove and remove all function"
            targetOutput.session.setValue("[1, 2, 3, 4, 5]");
            break;
        case "7":
            document.getElementById("problem").innerHTML = "Objective: Given the following variable: list, [1,3,4,6,7,8,9], check each index and write \"Even\" if the number is even, and \"Odd\" if the number is odd. Your program should make use of a loop for each and an if/otherwise statement to solve this problem."
            targetOutput.session.setValue("Odd\nOdd\nEven\nEven\nOdd\nEven\nOdd");  
            break;
        case "8":
            document.getElementById("problem").innerHTML = "Objective: Define variables \"index\" to be 0 and \"target\" to be 20. Use a loop to check \"index\" to see if it is even. If it is increment another variable \"count\" by 1. At the end of the loop increment \"index\" by 1. Repeat this process until \"index\" is equal to 20. I.E. do not run the loop if \"index\" is equal to 20."
            targetOutput.session.setValue("10");
            break;
        case "9":
            document.getElementById("problem").innerHTML = "Objective: Write a program which uses a function named \"addTwo\" to add two numbers together. Your program should contain the code:<br><code>addTwo(2, 2)<br>addTwo(5, 3)</code>"
            targetOutput.session.setValue("4\n8");
            break;
        case "10":
            document.getElementById("problem").innerHTML = "Objective: In the Fibonacci sequence, each number is the sum of the two preceding ones, starting from 0 and 1. The task is to write a recursive function called fibonacci that takes an integer n as an argument and returns the nth Fibonacci number. Your program should calculate the 4th and 10th fibonacci numbers.<br>E.g.<br><code>fibonacci(4)<br>fibonacci(10)</code>"
            targetOutput.session.setValue("3\n55");
            break;
        default:
            targetOutput.session.setValue("No target output available");
            break;
    }
}

function getMemory(){
    var tbody = document.getElementById("memoryBody");
    var tdElements = tbody.getElementsByTagName("td");

    var pairs = [];

    for (var i = 0; i < tdElements.length; i += 2) {
        var key = tdElements[i].textContent.trim();
        var value = tdElements[i + 1].textContent.trim();
        pairs.push({ key: key, value: value });
    }

    return pairs;
}

if (typeof module === 'object') {
    module.exports = {
        checkTargetOutput: checkTargetOutput,
        generateProblem: generateProblem,
        getMemory: getMemory
    };
}
