import { describe, expect, it } from 'vitest';
import { ApiError } from './apiError';
import { isProblem, ProblemType } from './problems';

const outdatedPolicy = new ApiError({
  type: ProblemType.businessRule,
  title: 'Business rule violated',
  status: 422,
  code: 'outdated-privacy-policy',
});

describe('isProblem', () => {
  it('matches the problem type and, when given, the business rule code', () => {
    expect(isProblem(outdatedPolicy, ProblemType.businessRule)).toBe(true);
    expect(isProblem(outdatedPolicy, ProblemType.businessRule, 'outdated-privacy-policy')).toBe(
      true,
    );
  });

  it('rejects another type, another code or an error that is not from the API', () => {
    expect(isProblem(outdatedPolicy, ProblemType.rateLimit)).toBe(false);
    expect(isProblem(outdatedPolicy, ProblemType.businessRule, 'invalid-title')).toBe(false);
    expect(isProblem(new TypeError('Failed to fetch'), ProblemType.businessRule)).toBe(false);
  });
});
