package ai.fasion.fabs.apollo.auth.vo;

public class MetaVO {

    private ApplicationVO application;

    public ApplicationVO getApplication() {
        return application;
    }

    public void setApplication(ApplicationVO application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "MetaVO{" +
                ", application=" + application +
                '}';
    }
}
