# EasyShell

## 描述

一个开源执行shell脚本的工具，纯Java实现，支持Windows、Linux、MacOS、Unix、Android等平台。

使用Kotlin语言编写，支持Java、Kotlin、Groovy等语言编写脚本。
无任何三方依赖，可直接使用。

| 平台      | CMD | PowerShell | Git Bash | Sh | Bash | Zsh | Su |
|---------|-----|------------|----------|----|------|-----|----|
| Windows | ✅   | ✅          | ✅        | ⭕️ | ⭕️   | ⭕️  | ⭕️ |
| Linux   | ⭕️  | ✅          | ⭕️       | ✅  | ✅    | ✅   | ✅  |
| MacOS   | ⭕️  | ✅          | ⭕️       | ✅  | ✅    | ✅   | ✅  |
| Unix    | ⭕️  | ✅          | ⭕️       | ✅  | ✅    | ✅   | ✅  |
| Android | ⭕️  | ⭕️         | ⭕️       | ✅  | ✅    | ✅   | ✅  |

> ⭕️：不支持
> 
> ✅：支持
> 

## 依赖

```groovy
implementation 'io.github.uhsk:shell:1.0.0'
```

## 使用

### Kotlin

```kotlin
val shell = Shell.Sh()  // Shell.PowerShell()  Shell.Bash() Shell.Zsh()  Shell.Su()
val result = shell.exec("echo 'Hello World!'")
if (!result.isSuccess) {
    println(result.output)
}
val result = shell.exec("ls -l")
if (!result.isSuccess) {
    println(result.output)
}
```

### Java

```java
Shell.Sh shell = new Shell.Sh();
Shell.Result result = shell.exec("echo 'Hello World!'");
if (!result.isSuccess()) {
    System.out.println(result.getOutput());
}
Shell.Result result = shell.exec("ls -l");
if (!result.isSuccess()) {
    System.out.println(result.getOutput());
}
```

## 进阶

### 自定义工作目录、环境变量

```kotlin
val dir: String = System.getProperty("user.dir")       // 自定义工作目录
val evn = mutableMapOf("PATH" to "/usr/local/bin")     // 自定义环境变量
val shell = Shell.Sh(dir, env)
```

### 自定义超时时间、输出编码、输出回调

```kotlin
val shell = Shell.Sh()
val command = Shell.Command("echo 'Hello World!'")
command.timeout = TimeUnit.SECONDS.toMillis(10) // 自定义超时时间
command.charset = Charsets.UTF_8                // 自定义输出编码
command.onLineOutputCallback = BiFunction { stream, line ->
    // 自定义输出回调
    // stream: 输入数据流，可根据line进行判断来向shell输入数据
    // line: 输出数据行
    println(line)
}
val result = shell.exec(command)
```

### 中断
    
```kotlin
val shell = Shell.Sh()
shell.interrupt()
```

### 关闭

```kotlin
val shell = Shell.Sh()
shell.close()
```

## 感谢

- [github/jaredrummler/KtSh](https://github.com/jaredrummler/KtSh)

## License

```
Copyright (C) 2023 Sollyu

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
