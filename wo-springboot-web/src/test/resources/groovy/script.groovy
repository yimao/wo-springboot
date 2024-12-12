package groovy

def hello_world() {
    println("Hello World");
    println("How are you ${name}");
    return UUID.randomUUID().toString();
}

hello_world();
