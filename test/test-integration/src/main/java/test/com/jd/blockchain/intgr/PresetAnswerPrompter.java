package test.com.jd.blockchain.intgr;

import java.util.Properties;

import com.jd.blockchain.tools.initializer.ConsolePrompter;

public class PresetAnswerPrompter extends ConsolePrompter {

	private Properties answers = new Properties();

	private String defaultAnswer;

	public PresetAnswerPrompter(String defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
	}

	public void setAnswer(String tag, String answer) {
		answers.setProperty(tag, answer);
	}

	public void setDefaultAnswer(String defaultAnswer) {
		this.defaultAnswer = defaultAnswer;
	}

	@Override
	public String confirm(String tag, String format, Object... args) {
		System.out.print(String.format(format, args));
		String answer = answers.getProperty(tag, defaultAnswer);
		System.out.println(String.format("\r\n   [Mocked answer:%s]", answer));
		return answer;
	}

}
