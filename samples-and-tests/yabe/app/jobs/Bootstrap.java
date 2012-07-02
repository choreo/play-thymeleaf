/**
 * 
 */
package jobs;

import org.thymeleaf.TemplateEngine;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.thymeleaf.ThymeleafPlugin;
import play.modules.thymeleaf.messageresolver.PlayMessageResolver;

/**
 * 初期化クラスです。 モジュールからテンプレートをロードするresolverと、playのmessagesを使う設定を登録します。
 */
@OnApplicationStart
public class Bootstrap extends Job<Object> {
	@Override
    public void doJob() {
		Play.plugin(ThymeleafPlugin.class).getTemplateEngine().addMessageResolver(new PlayMessageResolver());
    }
}
