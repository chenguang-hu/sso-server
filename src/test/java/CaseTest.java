import com.learn.sso.common.Config;
import com.learn.sso.SSOApplication;
import com.learn.sso.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SSOApplication.class)
public class CaseTest {

    @Resource
    private Config config;

    @Resource
    private UserDao userDao;

    @Test
    public void test() {
        System.out.println(userDao.findByUsername("admin").get("password").toString());
    }
}
