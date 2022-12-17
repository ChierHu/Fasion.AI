package ai.fasion.fabs.vesta.expansion;

import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.*;

/**
 * Function: 用java实现shell命令调用
 * 不允许执行长命令 什么意思呢，就是不能执行 top、tail -f这种长期占用的命令
 * 需要长命令的，会在另一个工具类中体现.
 *
 * @author miluo
 * Date: 2019-01-03 11:44
 * @since JDK 1.8
 */
public class LocalCommandExecutorImpl implements LocalCommandExecutor {


    public static void main(String[] args) {
        /*
        //cmd[] demo
        String[] arguments = new String[]{"python3", "/Users/bainingchen/Project/Java/ai.xingheng.toolbox/src/main/resources/demoPython.py", "1", "2"};
        LocalCommandExecutorImpl localCommandExecutor = new LocalCommandExecutorImpl();
        ExecuteResult executeResult = localCommandExecutor.executeCommand(arguments);
        System.out.println("退出码：" + executeResult.getExitCode() + "\n消息：\n" + executeResult.getExecuteOut());

        //cmd demo
        LocalCommandExecutorImpl localCommandExecutor = new LocalCommandExecutorImpl();
        ExecuteResult executeResult = localCommandExecutor.executeCommand("pwd",null,new File("/Users"));
        System.out.println("退出码：" + executeResult.getExitCode() + "\n消息：\n" + executeResult.getExecuteOut());
        */

        LocalCommandExecutorImpl localCommandExecutor = new LocalCommandExecutorImpl();
        ExecuteResult executeResult = localCommandExecutor.executeCommand("java -jar candlestick-1.0-SNAPSHOT.jar", null, new File("/Users/bainingchen/Project/Java/ai.xingheng.server.candlestick/target/"), 1000);
        System.out.println("退出码：" + executeResult.getExitCode() + "\n消息：\n" + executeResult.getExecuteOut());

    }

    //    static final Logger logger = LoggerFactory.getLogger(LocalCommandExecutorImpl.class);
    static ExecutorService pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 3L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    @Override
    public ExecuteResult executeCommand(String command) {
        return executeCommand(command, null, null, 1000);
    }

    @Override
    public ExecuteResult executeCommand(String command, long timeout) {
        return executeCommand(command, null, null, timeout);
    }

    @Override
    public ExecuteResult executeCommand(String command, String[] envp) {
        return executeCommand(command, envp, 1000);
    }

    @Override
    public ExecuteResult executeCommand(String command, String[] envp, long timeout) {
        return executeCommand(command, envp, null, timeout);
    }

    @Override
    public ExecuteResult executeCommand(String command, String[] envp, File dir) {
        return executeCommand(command, envp, dir, 1000);
    }

    @Override
    public ExecuteResult executeCommand(String command, String[] envp, File dir, long timeout) {
        Process process = null;
        InputStream pIn = null;
        InputStream pErr = null;
        StreamGobbler outputGobbler = null;
        StreamGobbler errorGobbler = null;
        Future<Integer> executeFuture = null;
        try {
//            System.out.println(command.toString());
//            logger.info(command.toString());
            process = Runtime.getRuntime().exec(command, envp, dir);
            final Process p = process;

            // close process's output stream.
            p.getOutputStream().close();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            pIn = process.getInputStream();
            outputGobbler = new StreamGobbler(pIn, "OUTPUT");
            outputGobbler.start();

            pErr = process.getErrorStream();
            errorGobbler = new StreamGobbler(pErr, "ERROR");
            errorGobbler.start();

            // create a Callable for the command's Process which can be called by an Executor
            Callable<Integer> call = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    p.waitFor();
                    return p.exitValue();
                }
            };

            // submit the command's call and get the result from a
            executeFuture = pool.submit(call);
            int exitCode = executeFuture.get(timeout, TimeUnit.MILLISECONDS);
            if (!StringUtils.isEmpty(outputGobbler.getContent())) {
                return new ExecuteResult(exitCode, outputGobbler.getContent());
            } else {
                return new ExecuteResult(exitCode, errorGobbler.getContent());
            }
        } catch (IOException ex) {
            String errorMessage = "The command [" + command + "] execute failed.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (TimeoutException ex) {
            String errorMessage = "The command [" + command + "] timed out.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (ExecutionException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an execution error.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (InterruptedException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an interrupted error.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } finally {
            if (executeFuture != null) {
                try {
                    executeFuture.cancel(true);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (pIn != null) {
                this.closeQuietly(pIn);
                if (outputGobbler != null && !outputGobbler.isInterrupted()) {
                    outputGobbler.interrupt();
                }
            }
            if (pErr != null) {
                this.closeQuietly(pErr);
                if (errorGobbler != null && !errorGobbler.isInterrupted()) {
                    errorGobbler.interrupt();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public ExecuteResult executeCommand(String[] command, long timeout) {
        return executeCommand(command, null, null, timeout);
    }

    @Override
    public ExecuteResult executeCommand(String[] command, String[] envp, File dir, long timeout) {
        Process process = null;
        InputStream pIn = null;
        InputStream pErr = null;
        StreamGobbler outputGobbler = null;
        StreamGobbler errorGobbler = null;
        Future<Integer> executeFuture = null;
        try {
//            System.out.println(command.toString());
//            logger.info(command.toString());
            process = Runtime.getRuntime().exec(command, envp, dir);
            final Process p = process;

            // close process's output stream.
            p.getOutputStream().close();

            // 获取命令执行结果, 有两个结果: 正常的输出 和 错误的输出（PS: 子进程的输出就是主进程的输入）
            pIn = process.getInputStream();
            outputGobbler = new StreamGobbler(pIn, "OUTPUT");
            outputGobbler.start();

            pErr = process.getErrorStream();
            errorGobbler = new StreamGobbler(pErr, "ERROR");
            errorGobbler.start();

            // create a Callable for the command's Process which can be called by an Executor
            Callable<Integer> call = new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    p.waitFor();
                    return p.exitValue();
                }
            };
            // submit the command's call and get the result from a
            executeFuture = pool.submit(call);
            int exitCode = executeFuture.get(timeout, TimeUnit.MILLISECONDS);
            if (!StringUtils.isEmpty(outputGobbler.getContent())) {
                return new ExecuteResult(exitCode, outputGobbler.getContent());
            } else {
                return new ExecuteResult(exitCode, errorGobbler.getContent());
            }
        } catch (IOException ex) {
            String errorMessage = "The command [" + command + "] execute failed.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (TimeoutException ex) {
            String errorMessage = "The command [" + command + "] timed out.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (ExecutionException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an execution error.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } catch (InterruptedException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an interrupted error.";
            System.err.println(errorMessage + "\t" + ex);
//            logger.error(errorMessage, ex);
            return new ExecuteResult(-1, null);
        } finally {
            if (executeFuture != null) {
                try {
                    executeFuture.cancel(true);
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
            if (pIn != null) {
                this.closeQuietly(pIn);
                if (outputGobbler != null && !outputGobbler.isInterrupted()) {
                    outputGobbler.interrupt();
                }
            }
            if (pErr != null) {
                this.closeQuietly(pErr);
                if (errorGobbler != null && !errorGobbler.isInterrupted()) {
                    errorGobbler.interrupt();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public ExecuteResult executeCommand(String[] command) {
        return executeCommand(command, null, null, 1000);
    }

    private void closeQuietly(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
//            logger.error("exception", e);
            System.err.println("exception\t" + e);
        }
    }
}
