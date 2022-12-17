package ai.fasion.fabs.vesta.expansion;

/**
 * Function: 命令执行结果
 *
 * @author miluo
 * Date: 2019-01-03 11:42
 * @since JDK 1.8
 */
public class ExecuteResult {
    private int exitCode;
    private String executeOut;

    public ExecuteResult(int exitCode, String executeOut) {
        this.exitCode = exitCode;
        this.executeOut = executeOut;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getExecuteOut() {
        return executeOut;
    }

    public void setExecuteOut(String executeOut) {
        this.executeOut = executeOut;
    }
}
