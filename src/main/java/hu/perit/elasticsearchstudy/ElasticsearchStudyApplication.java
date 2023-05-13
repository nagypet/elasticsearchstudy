package hu.perit.elasticsearchstudy;

import hu.perit.spvitamin.spring.SpvitaminApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"hu.perit.spvitamin", "hu.perit.elasticsearchstudy"})
public class ElasticsearchStudyApplication
{

	public static void main(String[] args) {
		SpvitaminApplication.run(ElasticsearchStudyApplication.class, args);
	}

}
