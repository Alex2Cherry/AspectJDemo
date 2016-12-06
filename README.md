
#Android中使用AspectJ的Demo

##IDE 
android studio

##目标
类似于服务端spring的aop,在android上使用非侵入方式实现切面编程

    OOP世界中，有些功能是横跨并嵌入众多模块里的，比如打印日志。这些功能在各个模块里分散得很厉害，可能到处都能见到。
    AOP的目标是把这些功能集中起来，放到一个统一的地方来控制和管理。如果说，OOP如果是把问题划分到单个模块的话，那么AOP就是把涉及到众多模块的某一类问题进行统一管理。
    比如我们可以设计两个Aspects，一个是管理某个软件中所有模块的日志输出的功能，另外一个是管理该软件中一些特殊函数调用的权限检查。

##用途

比较常见的用途有: 

- 打印日志
    
        在没有AOP之前，各个模块要打印日志，就是自己处理。反正日志模块的那几个API都已经写好了，你在其他模块的任何地方，任何时候都可以调用。
        随意加日志输出功能，使得其他模块的代码和日志模块耦合非常紧密。而且，将来要是日志模块修改了API，则使用它们的地方都得改。
        AOP方式使得我们可以在一个统一的地方进行日志管理
    
- 统计方法执行时间

        持续优化app的性能需要对代码执行时间进行监控,找出耗时的操作进行优化
        
- 权限检查

##局限性
由于aspectJ切面编程的原理采用的是编译时织入代码的方式,导致有以下缺陷

- 无法对父类的方法织入代码
- 无法对jar包、maven依赖的aar包织入代码
- 如果是封装成libraryModule的方式使用,通过project(":module_name")方式依赖的所有module,都需要依赖该module(可以通过传递依赖),并且build.gradle中都需要添加香烟的代码

所以,最佳使用环境为:

- 单个module的app
- 多个module的app但使用project的方式进行依赖

        
##使用方式

###1. 直接在application中使用的方式

- 首先,复制app/build.gradle中第60行以下的代码到需要使用AOP的module中(application的module,不是library的module)
- 创建Aspect类,例如: `demo.billy.com.aspectjdemo.aspectj.TestAspectJ.java`
- 编写pointcuts及advices
- 运行看结果

---

###2. 封装成libraryModule使用的方式

- 复制一下代码到library的build.gradle文件中

        import com.android.build.gradle.LibraryPlugin
        import org.aspectj.bridge.IMessage
        import org.aspectj.bridge.MessageHandler
        import org.aspectj.tools.ajc.Main
        
        buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath 'org.aspectj:aspectjtools:1.8.9'
                classpath 'org.aspectj:aspectjweaver:1.8.9'
            }
        }
        
        repositories {
            mavenCentral()
        }
        dependencies {
            compile 'org.aspectj:aspectjrt:1.8.9'
        }
        android.libraryVariants.all { variant ->
            LibraryPlugin plugin = project.plugins.getPlugin(LibraryPlugin)
            JavaCompile javaCompile = variant.javaCompile
            javaCompile.doLast {
                String[] args = ["-showWeaveInfo",
                                 "-1.5",
                                 "-inpath", javaCompile.destinationDir.toString(),
                                 "-aspectpath", javaCompile.classpath.asPath,
                                 "-d", javaCompile.destinationDir.toString(),
                                 "-classpath", javaCompile.classpath.asPath,
                                 "-bootclasspath", plugin.project.android.bootClasspath.join(
                        File.pathSeparator)]
        
                MessageHandler handler = new MessageHandler(true);
                new Main().run(args, handler)
        
                def log = project.logger
                for (IMessage message : handler.getMessages(null, true)) {
                    switch (message.getKind()) {
                        case IMessage.ABORT:
                        case IMessage.ERROR:
                        case IMessage.FAIL:
                            log.error message.message, message.thrown
                            break;
                        case IMessage.WARNING:
                        case IMessage.INFO:
                            log.info message.message, message.thrown
                            break;
                        case IMessage.DEBUG:
                            log.debug message.message, message.thrown
                            break;
                    }
                }
            }
        }

- 复制以下代码到application的build.gradle文件中

            import org.aspectj.bridge.IMessage
            import org.aspectj.bridge.MessageHandler
            import org.aspectj.tools.ajc.Main
            
            buildscript {
                repositories {
                    mavenCentral()
                }
                dependencies {
                    classpath 'org.aspectj:aspectjtools:1.8.9'
                }
            }
            
            dependencies {
                compile 'org.aspectj:aspectjrt:1.8.9'
            }
            final def log = project.logger
            final def variants = project.android.applicationVariants
            
            variants.all { variant ->
                if (!variant.buildType.isDebuggable()) {
                    log.debug("Skipping non-debuggable build type '${variant.buildType.name}'.")
                    return;
                }
            
                JavaCompile javaCompile = variant.javaCompile
                javaCompile.doLast {
                    String[] args = ["-showWeaveInfo",
                                     "-1.5",
                                     "-inpath", javaCompile.destinationDir.toString(),
                                     "-aspectpath", javaCompile.classpath.asPath,
                                     "-d", javaCompile.destinationDir.toString(),
                                     "-classpath", javaCompile.classpath.asPath,
                                     "-bootclasspath", project.android.bootClasspath.join(File.pathSeparator)]
                    log.debug "ajc args: " + Arrays.toString(args)
            
                    MessageHandler handler = new MessageHandler(true);
                    new Main().run(args, handler);
                    for (IMessage message : handler.getMessages(null, true)) {
                        switch (message.getKind()) {
                            case IMessage.ABORT:
                            case IMessage.ERROR:
                            case IMessage.FAIL:
                                log.error message.message, message.thrown
                                break;
                            case IMessage.WARNING:
                                log.warn message.message, message.thrown
                                break;
                            case IMessage.INFO:
                                log.info message.message, message.thrown
                                break;
                            case IMessage.DEBUG:
                                log.debug message.message, message.thrown
                                break;
                        }
                    }
                }
            }

- application的build.gradle中添加对library module的依赖
- 在library中创建Aspect
- 运行看结果
