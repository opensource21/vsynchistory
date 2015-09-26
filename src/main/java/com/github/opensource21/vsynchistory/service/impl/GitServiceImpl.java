package com.github.opensource21.vsynchistory.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.opensource21.vsynchistory.service.api.GitService;

/**
 * Everything to handle the git-repo.
 *
 * @author niels
 *
 */
@Service
public class GitServiceImpl implements GitService {

	@Value(value = "${repositoryLocation}")
	private String repositoryLocation;

	@Override
	public Set<String> getChangedFilenames() throws NoWorkTreeException,
			GitAPIException {
		final Set<String> result = new HashSet<String>();
		try (final Git git = getGit()) {
			final Status status = git.status().call();
			result.addAll(status.getChanged());
			result.addAll(status.getModified());
		}
		return result;
	}

	@Override
	public ContentTupel getContents(String filename) throws GitAPIException,
			IOException {
		try (final Repository repository = getRepository();
				final TreeWalk treeWalk = new TreeWalk(repository);
				final RevWalk revWalk = new RevWalk(repository);) {

			// find the HEAD
			final ObjectId lastCommitId = repository.resolve(Constants.HEAD);
			// now we have to get the commit
			final RevCommit commit = revWalk.parseCommit(lastCommitId);
			// and using commit's tree find the path
			final RevTree tree = commit.getTree();
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(PathFilter.create(filename));
			if (!treeWalk.next()) {
				return null;
			}
			final ObjectId objectId = treeWalk.getObjectId(0);
			final ObjectLoader loader = repository.open(objectId);

			// and then one can use either
			final InputStream oldInputStream = loader.openStream();
			final InputStream newInputStream = new FileInputStream(new File(repository.getDirectory().getParentFile(), filename));
			revWalk.dispose();
			return new ContentTupel(oldInputStream, newInputStream);
		}
	}

	@Override
	public void commit(String commitMessage, String... filenames)
			throws GitAPIException {
		try (final Git git = getGit()) {
			final AddCommand addCommand = git.add();
			for (final String filename : filenames) {
				addCommand.addFilepattern(filename);
			}
			addCommand.call();
			git.commit().setMessage(commitMessage).call();
		}
	}

	private final Git getGit() {
		return new Git(getRepository());
	}

	private Repository getRepository() {

		File repoDir = new File(repositoryLocation);
		if (!repoDir.getName().endsWith("git")) {
			repoDir = new File(repoDir, ".git");
		}
		try {
			final FileRepositoryBuilder builder = new FileRepositoryBuilder();
			return builder.setGitDir(repoDir).readEnvironment() // scan
																// environment
																// GIT_*
																// variables
					.findGitDir() // scan up the file system tree
					.build();
		} catch (final IOException ioE) {
			throw new IllegalStateException("The repository "
					+ repoDir.getAbsolutePath() + " could not be accessed.",
					ioE);
		}
	}

}
