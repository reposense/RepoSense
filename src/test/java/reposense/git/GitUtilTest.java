package reposense.git;

import static reposense.git.GitUtil.convertToGitExcludeGlobArgs;
import static reposense.util.StringsUtil.addQuotes;

import java.io.File;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import reposense.template.GitTestTemplate;

public class GitUtilTest extends GitTestTemplate {

    @Test
    public void gitUtil_convertToGitExcludeGlobArgs_success() {
        File repoRoot = new File(config.getRepoRoot());
        final String cmdFormat = " " + addQuotes(":(exclude)%s");
        final String emptyResult = "";

        String result = convertToGitExcludeGlobArgs(repoRoot, Collections.emptyList());
        Assert.assertEquals(emptyResult, result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("**.js"));
        Assert.assertEquals(String.format(cmdFormat, "**.js"), result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("movedFile**"));
        Assert.assertEquals(String.format(cmdFormat, "movedFile**"), result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("*\\newPos"));
        Assert.assertEquals(String.format(cmdFormat, "*\\newPos"), result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("newPos/*"));
        Assert.assertEquals(String.format(cmdFormat, "newPos/*"), result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("../**"));
        Assert.assertEquals(emptyResult, result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("\\**"));
        Assert.assertEquals(emptyResult, result);

        result = convertToGitExcludeGlobArgs(repoRoot, Collections.singletonList("/newPos/*"));
        Assert.assertEquals(emptyResult, result);
    }
}
