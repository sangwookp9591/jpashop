package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}
	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		//모듈만 하게될경우에는 lazy로 설정된것은 다 null로 리턴
		//모듈 자체는 필요할수 있지만 엔티티는 외부로 호출하면 안된다.

		//hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		//Json 호출하는 시점에 Lazy 로딩을 해버림 -> 데이터가 다나오게됨
		return hibernate5Module;
	}

}
