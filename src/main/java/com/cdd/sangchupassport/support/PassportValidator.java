package com.cdd.sangchupassport.support;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import com.cdd.sangchupassport.Passport;
import com.cdd.sangchupassport.exception.PassportErrorCode;
import com.cdd.sangchupassport.exception.PassportException;
import com.cdd.sangchupassport.token.PassportToken;

@Component
public class PassportValidator {
	/**
	 * Redis Repository
	 */
	private final CrudRepository<PassportToken, String> repository;

	public PassportValidator(
		final CrudRepository<PassportToken, String> repository
	) {
		this.repository = repository;
	}

	/**
	 * `Passport`가 만료되었는지, 재사용되었는지 확인합니다.
	 *
	 * @param passport 검증되는 인증 객체
	 */
	public void validatePassport(
		final Passport passport
	) {
		validateExpiredTime(passport);
		validateReuse(passport);
	}

	private void validateExpiredTime(
		final Passport passport
	) {
		long now = System.currentTimeMillis();
		if (now > passport.getExpiredTime()) {
			throw new PassportException(passport, PassportErrorCode.EXPIRED_PASSPORT);
		}
	}

	private void validateReuse(
		final Passport passport
	) {
		if (repository.existsById(passport.getId())) {
			throw new PassportException(passport, PassportErrorCode.REUSE_PASSPORT);
		}
	}
}
