package reposense.git;

import static reposense.git.GitUtil.convertToGitExcludeGlobArgs;
import static reposense.util.StringsUtil.addQuote;

import java.io.File;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitUtilTest extends GitTestTemplate {

    @Test
    public void gitUtil_convertToGitExcludeGlobArgs_success() {
        File repoRoot = new File(config.getRepoRoot());
        final String cmdFormat = " " + addQuote(":(exclude)%s");
        final String emptyResult = "";

        String result = convertToGitExcludeGlobArgs(repoRoot, Collections.EMPTY_LIST);
        Assert.assertEquals(result, emptyResult);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("**.js"));
        Assert.assertEquals(result, String.format(cmdFormat, "**.js"));

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("collate**"));
        Assert.assertEquals(result, String.format(cmdFormat, "collate**"));

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("*\\sth"));
        Assert.assertEquals(result, String.format(cmdFormat, "*\\sth"));

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("bin/*"));
        Assert.assertEquals(result, String.format(cmdFormat, "bin/*"));

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("../**"));
        Assert.assertEquals(result, emptyResult);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("\\**"));
        Assert.assertEquals(result, emptyResult);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("/sth/*"));
        Assert.assertEquals(result, emptyResult);
    }
}
