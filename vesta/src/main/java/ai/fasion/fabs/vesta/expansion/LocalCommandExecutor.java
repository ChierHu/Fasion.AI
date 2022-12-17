package ai.fasion.fabs.vesta.expansion;

import java.io.File;

/**
 * Function:
 * envp: 字符串数组，其中每个元素的环境变量的设置格式为name=value；如果子进程应该继承当前进程的环境，则该参数为 null。
 * dir: 子进程的工作目录；如果子进程应该继承当前进程的工作目录，则该参数为 null。
 *
 * @author miluo
 * Date: 2019-01-03 11:43
 * @since JDK 1.8
 */
public interface LocalCommandExecutor {

    /**
     * 在单独的进程中执行指定的字符串命令。
     * Process exec(String command)
     *
     * @param command 命令
     * @return
     */
    ExecuteResult executeCommand(String command);


    /**
     * 在单独的进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @param timeout 超时时间
     * @return
     */
    ExecuteResult executeCommand(String command, long timeout);

    /**
     * 在指定环境的单独进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @param envp    环境变量 key=value 形式
     * @return
     */
    ExecuteResult executeCommand(String command, String[] envp);

    /**
     * 在指定环境的单独进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @param envp    环境变量 key=value 形式
     * @param timeout 超时时间
     * @return
     */
    ExecuteResult executeCommand(String command, String[] envp, long timeout);

    /**
     * 在有指定环境和工作目录的独立进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @param envp    字符串数组，其中每个元素的环境变量的设置格式为name=value；如果子进程应该继承当前进程的环境，则该参数为 null。
     * @param dir     子进程的工作目录；如果子进程应该继承当前进程的工作目录，则该参数为 null。
     * @return
     */
    ExecuteResult executeCommand(String command, String[] envp, File dir);

    /**
     * 在有指定环境和工作目录的独立进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @param timeout 超时时间
     * @return
     */
    ExecuteResult executeCommand(String command, String[] envp, File dir, long timeout);

    /**
     * 在有指定环境和工作目录的独立进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @param timeout 超时时间
     * @return
     */
    ExecuteResult executeCommand(String[] command, long timeout);

    /**
     * 在有指定环境和工作目录的独立进程中执行指定的字符串命令。
     *
     * @param command 命令
     * @return
     */
    ExecuteResult executeCommand(String[] command);

    /**
     * @param command 命令
     * @param envp    需要的环境命令
     * @param dir     执行的目录
     * @param timeout 超时时间
     * @return
     */
    ExecuteResult executeCommand(String[] command, String[] envp, File dir, long timeout);
}
